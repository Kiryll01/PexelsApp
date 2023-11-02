package com.example.pexelsapp.pagination

import androidx.paging.*
import androidx.room.withTransaction
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Database.PexelsAppDatabase
import com.example.pexelsapp.Web.PexelsApiClient
import com.example.pexelsapp.Web.PexelsApiService
import com.example.pexelsapp.Web.PexelsSearchResponse

@OptIn(ExperimentalPagingApi::class)
class PexelsRemoteMediator(
    private val apiCall : suspend (page : Int, perPage : Int) -> PexelsSearchResponse,
    private val db : PexelsAppDatabase
) : RemoteMediator<Int,PexelsPhotoEntity>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PexelsPhotoEntity>): MediatorResult {
        return try {
            val pageIndex = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) 1
                    else {
                        val lastPageIndex = state.pages.indexOfLast {
                            it.data.contains(lastItem)
                        }
                        lastPageIndex + 1
                    }

                }
            }
            val response = apiCall.invoke(pageIndex, state.config.pageSize)

            val entities = response.photos.map {
                it.asEntity()
            }

            if (loadType == LoadType.REFRESH) db.photosDao().refresh(entities)
            else db.photosDao().insertAll(entities)
            
            MediatorResult.Success(
                endOfPaginationReached = entities.isEmpty()
            )
        }
        catch (e : Exception){
            return MediatorResult.Error(e)
        }


    }
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}