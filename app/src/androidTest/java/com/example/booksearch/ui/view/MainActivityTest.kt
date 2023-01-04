package com.example.booksearch.ui.view

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        //액티비티 시나리오 사용
        //테스트용 액티비티
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        //액티비티 시나리로 파괴
        activityScenario.close()
    }

    @Test
    @SmallTest
    fun test_activity_state() {
        //액티비티 상태가 RESUMED상태인지 테스트
        val activityState = activityScenario.state.name
        assertThat(activityState).isEqualTo("RESUMED")
    }
}