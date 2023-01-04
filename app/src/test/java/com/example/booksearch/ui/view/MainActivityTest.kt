package com.example.booksearch.ui.view

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

//액티비티를 로컬 유닛테스트로 할 수 있음
//RobolectricTestRunner 로 인해
//@RunWith(RobolectricTestRunner::class)
//class MainActivityTest{
//
//    //로버레트릭이 4.0으로 업그레이드되면서 통합됨
//    //setupActivity 는 사용되지 않고 제거됨.
//    @Test
//    @SmallTest
//    fun test_activity_state() {
//        val controller = Robolectric.setupActivity(MainActivity::class.java)
//        val activityState = controller.lifecycle.currentState.name
//        assertThat(activityState).isEqualTo("RESUMED")
//    }
//}

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MainActivityTest {

//    private lateinit var activityScenario: ActivityScenario<MainActivity>
//
//    @Before
//    fun setUp() {
//        //액티비티 시나리오 사용
//        //테스트용 액티비티
//        activityScenario = ActivityScenario.launch(MainActivity::class.java)
//    }
//
//    @After
//    fun tearDown() {
//        //액티비티 시나리로 파괴
//        activityScenario.close()
//    }


    //매 태스트 마다 시나리오 룰에 따라 시나리오가 생성되고, 파괴가 됩니다.
    @get:Rule
    var activityScenarioRule : ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    @SmallTest
    fun test_activity_state() {
        //액티비티 상태가 RESUMED상태인지 테스트
//        val activityState = activityScenario.state.name
        val activityState = activityScenarioRule.scenario.state.name
        assertThat(activityState).isEqualTo("RESUMED")
    }
}