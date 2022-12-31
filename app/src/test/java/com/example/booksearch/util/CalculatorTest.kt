package com.example.booksearch.util

import androidx.test.filters.SmallTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before

@SmallTest
class CalculatorTest{
//    private val calculator = Calculator()

    private lateinit var calculator : Calculator

    //실행 직전에 수행되어야할 작업을 정의함
    @Before
    fun setUp(){
        calculator = Calculator()
    }

    //테스트 직후 수행되어야할 작업을 정의함
    @After
    fun tearDown(){

    }

    @Test
    fun `additional function test`(){
        //Given
        val x = 4
        val y = 2

        //When
        val result = calculator.addition(x, y)

        //Then
        assertThat(result).isEqualTo(6)
    }

    @Test
    fun `subtraction function test`(){
        //Given
        val x = 4
        val y = 2

        //When
        val result = calculator.subtraction(x, y)

        //Then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `multiplication function test`(){
        //Given
        val x = 4
        val y = 2

        //When
        val result = calculator.multiplication(x, y)

        //Then
        assertThat(result).isEqualTo(8)
    }

    @Test
    fun `division function test`(){
        //Given
        val x = 4
        val y = 0

        //When
        val result = calculator.division(x, y)

        //Then
        assertThat(result).isEqualTo(null)
    }

}