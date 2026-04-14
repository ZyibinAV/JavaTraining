package com.homeapp.javatraining.session;

import com.homeapp.javatraining.model.InterviewState;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Session Utils Tests")
class SessionUtilsTest {

    @Mock
    private HttpSession session;

    @Test
    @DisplayName("Should return true when interview state exists in session")
    void hasInterview_withInterviewState_shouldReturnTrue() {
        // Arrange
        InterviewState state = new InterviewState(java.util.Set.of(), java.util.List.of());
        when(session.getAttribute(SessionUtils.INTERVIEW_STATE)).thenReturn(state);

        // Act
        boolean result = SessionUtils.hasInterview(session);

        // Assert
        assertThat(result).isTrue();
        verify(session).getAttribute(SessionUtils.INTERVIEW_STATE);
    }

    @Test
    @DisplayName("Should return false when interview state does not exist in session")
    void hasInterview_withoutInterviewState_shouldReturnFalse() {
        // Arrange
        when(session.getAttribute(SessionUtils.INTERVIEW_STATE)).thenReturn(null);

        // Act
        boolean result = SessionUtils.hasInterview(session);

        // Assert
        assertThat(result).isFalse();
        verify(session).getAttribute(SessionUtils.INTERVIEW_STATE);
    }

    @Test
    @DisplayName("Should return false when session is null")
    void hasInterview_withNullSession_shouldReturnFalse() {
        // Act
        boolean result = SessionUtils.hasInterview(null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should get interview state from session")
    void getInterviewState_withExistingState_shouldReturnState() {
        // Arrange
        InterviewState expectedState = new InterviewState(java.util.Set.of(), java.util.List.of());
        when(session.getAttribute(SessionUtils.INTERVIEW_STATE)).thenReturn(expectedState);

        // Act
        InterviewState result = SessionUtils.getInterviewState(session);

        // Assert
        assertThat(result).isEqualTo(expectedState);
        verify(session).getAttribute(SessionUtils.INTERVIEW_STATE);
    }

    @Test
    @DisplayName("Should return null when interview state does not exist")
    void getInterviewState_withoutState_shouldReturnNull() {
        // Arrange
        when(session.getAttribute(SessionUtils.INTERVIEW_STATE)).thenReturn(null);

        // Act
        InterviewState result = SessionUtils.getInterviewState(session);

        // Assert
        assertThat(result).isNull();
        verify(session).getAttribute(SessionUtils.INTERVIEW_STATE);
    }

    @Test
    @DisplayName("Should set interview state in session")
    void setInterviewState_withValidState_shouldSetState() {
        // Arrange
        InterviewState state = new InterviewState(java.util.Set.of(), java.util.List.of());

        // Act
        SessionUtils.setInterviewState(session, state);

        // Assert
        verify(session).setAttribute(SessionUtils.INTERVIEW_STATE, state);
    }

    @Test
    @DisplayName("Should clear interview state from session")
    void clearInterview_withExistingState_shouldRemoveState() {
        // Act
        SessionUtils.clearInterview(session);

        // Assert
        verify(session).removeAttribute(SessionUtils.INTERVIEW_STATE);
    }

    @Test
    @DisplayName("Should handle null session in getInterviewState")
    void getInterviewState_withNullSession_shouldThrowException() {
        // Act & Assert - should throw NullPointerException
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> SessionUtils.getInterviewState(null)
        );
    }

    @Test
    @DisplayName("Should handle null session in setInterviewState")
    void setInterviewState_withNullSession_shouldThrowException() {
        // Arrange
        InterviewState state = new InterviewState(java.util.Set.of(), java.util.List.of());

        // Act & Assert - should throw NullPointerException
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> SessionUtils.setInterviewState(null, state)
        );
    }

    @Test
    @DisplayName("Should handle null session in clearInterview")
    void clearInterview_withNullSession_shouldThrowException() {
        // Act & Assert - should throw NullPointerException
        org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> SessionUtils.clearInterview(null)
        );
    }
}
