package com.example.seriesapp

import com.example.seriesapp.models.User
import com.example.seriesapp.viewModel.SignUpEvent
import com.example.seriesapp.viewModel.SignUpState
import com.example.seriesapp.viewModel.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate

class TestableSignUpViewModel : SignUpViewModel() {
    fun setTestState(state: SignUpState) {
        (this.state as MutableStateFlow).value = state
    }

    public override fun createUser(): User {
        val formattedBirthDate = state.value.birthDate?.let {
            LocalDate.parse(it).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } ?: ""

        return User(
            id = 42,
            name = state.value.username,
            password = state.value.password,
            birthDate = formattedBirthDate,
            isPolicyAccepted = state.value.isPolicyAccepted
        )
    }
}

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class SignUpViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TestableSignUpViewModel

    private val validUsername = "testUser"
    private val validPassword = "password123"
    private val validBirthDateString = "2000-01-01"
    private val validBirthDate = "2000-01-01"
    private val invalidBirthDateString = "invalid-date"

    @Before
    fun setup() {
        viewModel = TestableSignUpViewModel()
    }

    @Test
    fun `test initial state`() = runTest {
        with(viewModel.state.value) {
            assertEquals("", username)
            assertEquals("", password)
            assertEquals("", birthDateString)
            assertNull(birthDate)
            assertFalse(isPolicyAccepted)
            assertFalse(isSuccess)
            assertNull(currentUser)
        }
    }

    @Test
    fun `test UpdateUsername event updates username in state`() = runTest {
        viewModel.onEvent(SignUpEvent.UpdateUsername(validUsername))
        assertEquals(validUsername, viewModel.state.value.username)
    }

    @Test
    fun `test UpdatePassword event updates password in state`() = runTest {
        viewModel.onEvent(SignUpEvent.UpdatePassword(validPassword))
        assertEquals(validPassword, viewModel.state.value.password)
    }

    @Test
    fun `test UpdateBirthDate event with valid date updates birthDate in state`() = runTest {
        viewModel.onEvent(SignUpEvent.UpdateBirthDate(validBirthDateString))
        assertEquals(validBirthDateString, viewModel.state.value.birthDateString)
        assertEquals(validBirthDate, viewModel.state.value.birthDate)
    }

    @Test
    fun `test UpdatePolicyAccepted event updates isPolicyAccepted in state`() = runTest {
        viewModel.onEvent(SignUpEvent.UpdatePolicyAccepted(true))
        assertTrue(viewModel.state.value.isPolicyAccepted)
    }

    @Test
    fun `test SignUp event with valid form updates isSuccess and creates user`() = runTest {
        val validState = SignUpState(
            username = validUsername,
            password = validPassword,
            birthDate = validBirthDate,
            isPolicyAccepted = true
        )
        viewModel.setTestState(validState)
        viewModel.onEvent(SignUpEvent.SignUp)
        assertTrue(viewModel.state.value.isSuccess)
        assertNotNull(viewModel.state.value.currentUser)
        with(viewModel.state.value.currentUser!!) {
            assertEquals(validUsername, name)
            assertEquals(validPassword, password)
            assertEquals(validBirthDate, birthDate)
            assertTrue(isPolicyAccepted)
        }
    }

    @Test
    fun `test SignUp event with invalid form does not update state`() = runTest {
        val invalidState = SignUpState(
            username = validUsername,
            password = "",
            birthDateString = validBirthDateString,
            birthDate = validBirthDate,
            isPolicyAccepted = true
        )
        viewModel.setTestState(invalidState)
        viewModel.onEvent(SignUpEvent.SignUp)
        assertFalse(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.currentUser)
    }

    @Test
    fun `test isFormValid returns true with valid form data`() {
        val validState = SignUpState(
            username = validUsername,
            password = validPassword,
            birthDateString = validBirthDateString,
            birthDate = validBirthDate,
            isPolicyAccepted = true
        )
        assertTrue(validState.isFormValid())
    }

    @Test
    fun `test isFormValid returns false with empty username`() {
        val invalidState = SignUpState(
            username = "",
            password = validPassword,
            birthDateString = validBirthDateString,
            birthDate = validBirthDate,
            isPolicyAccepted = true
        )
        assertFalse(invalidState.isFormValid())
    }

    @Test
    fun `test isFormValid returns false with empty password`() {
        val invalidState = SignUpState(
            username = validUsername,
            password = "",
            birthDateString = validBirthDateString,
            birthDate = validBirthDate,
            isPolicyAccepted = true
        )
        assertFalse(invalidState.isFormValid())
    }

    @Test
    fun `test isFormValid returns false with null birthDate`() {
        val invalidState = SignUpState(
            username = validUsername,
            password = validPassword,
            birthDateString = invalidBirthDateString,
            birthDate = null,
            isPolicyAccepted = true
        )

        assertFalse(invalidState.isFormValid())
    }

    @Test
    fun `test isFormValid returns false when policy not accepted`() {
        val invalidState = SignUpState(
            username = validUsername,
            password = validPassword,
            birthDateString = validBirthDateString,
            birthDate = validBirthDate,
            isPolicyAccepted = false
        )

        assertFalse(invalidState.isFormValid())
    }

    @Test
    fun `test sequence of events leads to valid form state`() = runTest {
        viewModel.onEvent(SignUpEvent.UpdateUsername(validUsername))
        viewModel.onEvent(SignUpEvent.UpdatePassword(validPassword))
        viewModel.onEvent(SignUpEvent.UpdateBirthDate(validBirthDateString))
        viewModel.onEvent(SignUpEvent.UpdatePolicyAccepted(true))

        with(viewModel.state.value) {
            assertEquals(validUsername, username)
            assertEquals(validPassword, password)
            assertEquals(validBirthDateString, birthDateString)
            assertEquals(validBirthDate, birthDate)
            assertTrue(isPolicyAccepted)
            assertTrue(isFormValid())
        }

        viewModel.onEvent(SignUpEvent.SignUp)

        assertTrue(viewModel.state.value.isSuccess)
        assertNotNull(viewModel.state.value.currentUser)
    }
}