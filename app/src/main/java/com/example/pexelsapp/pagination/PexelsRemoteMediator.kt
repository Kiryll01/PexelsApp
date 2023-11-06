package com.example.pexelsapp.pagination

import android.nfc.Tag
import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Entitites.RemoteKeyEntity
import com.example.pexelsapp.Database.PexelsAppDatabase
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import com.example.pexelsapp.Web.PexelsSearchResponse

private const val TAG = "REMOTE_MEDIATOR"
@OptIn(ExperimentalPagingApi::class)
class PexelsRemoteMediator(
    private val apiCall : suspend (page : Int, perPage : Int) -> PexelsSearchResponse,
    private val db : PexelsAppDatabase
) : RemoteMediator<Int,PexelsPhotoEntity>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PexelsPhotoEntity>): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "refresh is called")
                    1
//                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
//                    remoteKey?.nextPage?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    Log.d(TAG, "prepend is called")
                    return MediatorResult.Success(endOfPaginationReached = true)
//                    val remoteKey = getRemoteKeyForFirstItem(state)
//                    val prevPage = remoteKey?.prevPage ?: return MediatorResult.Success(
//                        endOfPaginationReached = remoteKey != null
//                    )
//                    prevPage
                }

                LoadType.APPEND -> {
                    Log.d(TAG,"append is called")
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKey?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null
                    )
                    nextPage
                }
            }

            val response = apiCall.invoke(currentPage, PexelsApiService.PEXELS_PAGE_SIZE)
            val endOfPaginationReached = response.photos.isEmpty()

            Log.d(TAG , "end of pagination reached : $endOfPaginationReached")
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.photosDao().deleteUnliked()
                    db.keysDao().deleteAll()
                }
                val keys = response.photos.map {
                    RemoteKeyEntity(it.id, prevPage, nextPage)
                }
                db.keysDao().insertAll(keys)
                db.photosDao().insertAll(response.photos.map { it.asEntity() })

            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }
        catch (e:Exception){
           return MediatorResult.Error(e)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state : PagingState<Int,PexelsPhotoEntity>
    ) : RemoteKeyEntity?{
        return state.anchorPosition?.let { position->
            state.closestItemToPosition(position)?.id?.let { id->
                db.keysDao().getById(id)
            }
        }
    }


    private suspend fun getRemoteKeyForFirstItem(
        state : PagingState<Int,PexelsPhotoEntity>
    ) : RemoteKeyEntity? {
        return state.pages.firstOrNull{
            it.data.isNotEmpty()}?.data?.firstOrNull()
            ?.let { image->
                db.keysDao().getById(image.id)
            }
        }
    private suspend fun getRemoteKeyForLastItem(
        state : PagingState<Int,PexelsPhotoEntity>
    ) : RemoteKeyEntity?{
        val ids = ArrayList<Int>(30)
        state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.forEach{
            ids.add(it.id)
        }
        return db.keysDao().getById(ids).maxByOrNull{ it?.nextPage ?: Int.MIN_VALUE }

    }
//        return state.pages.lastOrNull{it.data.isNotEmpty()}?.data?.lastOrNull()
//            ?.let { image->
//                db.keysDao().getById(image.id)
//            }
//    }
}
