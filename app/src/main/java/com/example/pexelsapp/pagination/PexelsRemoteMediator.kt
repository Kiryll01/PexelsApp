package com.example.pexelsapp.pagination

import android.nfc.Tag
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.*
import androidx.room.withTransaction
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Entitites.RemoteKeyEntity
import com.example.pexelsapp.Database.PexelsAppDatabase
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import com.example.pexelsapp.Web.PexelsSearchResponse
import java.time.Instant
import kotlin.properties.Delegates

private const val TAG = "REMOTE_MEDIATOR"
@OptIn(ExperimentalPagingApi::class)
class PexelsRemoteMediator(
    private val apiCall : suspend (page : Int, perPage : Int) -> PexelsSearchResponse,
    private val db : PexelsAppDatabase,
    private val queryParam : String? = null,
    private val isCuratedCall: Boolean = false
) : RemoteMediator<Int,PexelsPhotoEntity>() {

    private var prevCall : Long = 0
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PexelsPhotoEntity>): MediatorResult {
        val currentCall = System.currentTimeMillis()
        if(currentCall-prevCall<1000) return MediatorResult.Success(endOfPaginationReached = false)
        prevCall=currentCall
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "refresh is called")
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    Log.d(TAG, "prepend is called")
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val endOfPaginationReached = remoteKeys != null
                    Log.d(TAG, "endOfPagination reached : $endOfPaginationReached")
                    val prevKey = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(endOfPaginationReached = false)
                    prevKey
                }

                LoadType.APPEND -> {
                    Log.d(TAG, "append is called")
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val endOfPaginationReached= remoteKey != null
                    Log.d(TAG,"endOfPaginationReached $endOfPaginationReached")
                    val nextPage = remoteKey?.nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = endOfPaginationReached
                    )
                    nextPage
                }
            }

            Log.d(TAG, "currentPage : $currentPage")
            val response = apiCall.invoke(currentPage, PexelsApiService.PEXELS_PAGE_SIZE)
            val endOfPaginationReached = response.photos.isEmpty()

            Log.d(TAG, "end of pagination reached : $endOfPaginationReached")
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    //TODO : just for test deleting all
                    db.photosDao().deleteAll()
                    db.keysDao().deleteAll()
                }
                val keys = response.photos.map {
                    RemoteKeyEntity(it.id, prevPage = prevPage, nextPage = nextPage)
                }
                db.keysDao().insertAll(keys)
                db.photosDao().insertAll(response.photos.map {
                        val entity= it.asEntity()
                        entity.queryParam=queryParam
                        entity.isCurated=isCuratedCall
                        entity
                })

            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PexelsPhotoEntity>
    ): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.keysDao().getById(id)
            }
        }
    }


    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, PexelsPhotoEntity>
    ): RemoteKeyEntity? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()
            ?.let { image ->
                db.keysDao().getById(image.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, PexelsPhotoEntity>
    ): RemoteKeyEntity? {
       return state.pages.lastOrNull{
           it.data.isNotEmpty()
       }?.data?.lastOrNull()
           ?.let { image->
               db.keysDao().getById(image.id)
           }
    }
}
