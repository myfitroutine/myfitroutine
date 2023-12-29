package com.bestteam.myfitroutine

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bestteam.myfitroutine.Model.WeightData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import com.bestteam.myfitroutine.Repository.MainRepository
import com.bestteam.myfitroutine.Repository.YoutubeRepository
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Mock
    lateinit var mockRepository: MainRepository
    lateinit var mockYoutubeRepo: YoutubeRepository

    lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(mockRepository,mockYoutubeRepo)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testGetTodayWeight() = runBlockingTest {
        // Mock 데이터 설정
        val expectedWeight = 65 // 예상되는 체중 값
        `when`(mockRepository.getTodayWeight()).thenReturn(WeightData("",expectedWeight,""))

        // 메소드 실행
        viewModel.getTodayWeight()

        // 검증
        verify(mockRepository).getTodayWeight()
        assert(viewModel.todayWeight.value == expectedWeight)
    }
}