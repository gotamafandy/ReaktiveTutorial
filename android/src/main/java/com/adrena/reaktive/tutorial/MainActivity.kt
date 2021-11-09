package com.adrena.reaktive.tutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.adrena.core.data.MoviesCloudService
import com.adrena.core.data.MoviesMapper
import com.adrena.core.data.cache.DatabaseHelper
import com.adrena.core.data.cache.MovieSqlCache
import com.adrena.core.data.repository.MoviesRepositoryImpl
import com.adrena.core.domain.UseCaseImpl
import com.adrena.core.presentation.ListViewModel
import com.adrena.core.presentation.ListViewModelImpl
import com.adrena.core.presentation.ViewModelBinding
import com.adrena.core.sql.MoviesDatabase
import com.adrena.reaktive.tutorial.model.MovieModel
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.facebook.stetho.Stetho
import com.squareup.sqldelight.android.AndroidSqliteDriver

class MainActivity : AppCompatActivity() {
    private lateinit var mMoviesAdapter: MoviesAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRefreshLayout: SwipeRefreshLayout

    private var mIsRefreshing = false
    private val mBinding = ViewModelBinding()

    // This should be singleton
    private val dbHelper: DatabaseHelper by lazy {
        val driver = AndroidSqliteDriver(
            schema = MoviesDatabase.Schema,
            context = this,
            name = "movie.db")

        DatabaseHelper("movie.db", driver)
    }

    private val mViewModel: ListViewModel<String, MovieModel> by lazy {

        val domainMapper = MoviesMapper()

        val service = MoviesCloudService("b445ca0b", "https://www.omdbapi.com/", domainMapper)

        val cache = MovieSqlCache(dbHelper)

        val repository = MoviesRepositoryImpl(service, cache)

        val useCase = UseCaseImpl(repository)

        val modelMapper = MovieModelsMapper()

        ListViewModelImpl(useCase, modelMapper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding()

        super.onCreate(savedInstanceState)

        Stetho.initializeWithDefaults(this)

        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.listing)
        mRefreshLayout = findViewById(R.id.refresh_layout)

        mMoviesAdapter = MoviesAdapter()

        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.adapter = mMoviesAdapter
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val manager = mRecyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = manager.itemCount
                val lastVisibleItem = manager.findLastVisibleItemPosition()

                if (!mIsRefreshing && totalItemCount <= lastVisibleItem + 2) {
                    loadMore()
                }
            }
        })

        mRefreshLayout.setOnRefreshListener {
            mViewModel.inputs.get("avenger")
        }

        mViewModel.inputs.get("avenger")
    }

    override fun onDestroy() {
        mBinding.dispose()

        super.onDestroy()
    }

    private fun binding() {
        mBinding.subscribe(mViewModel.outputs.loading.observeOn(mainScheduler), onNext = ::loading)
        mBinding.subscribe(mViewModel.outputs.result.observeOn(mainScheduler), onNext = ::result)
    }

    private fun loading(isLoading: Boolean) {
        mIsRefreshing = isLoading

        mRefreshLayout.isRefreshing = isLoading
    }

    private fun result(movies: List<MovieModel>) {
        mMoviesAdapter.setList(movies)
    }

    private fun loadMore() {
        mViewModel.inputs.loadMore("avenger")
    }
}
