package com.brokerx.auth_service.adapter.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brokerx.auth_service.domain.exception.user.UserException;
import com.brokerx.auth_service.domain.exception.refreshToken.RefreshTokenException;
import com.brokerx.auth_service.domain.exception.otpCode.OtpException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException by adding error message to flash attributes and redirecting to dashboard.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard/home";
    }

    /**
     * Handles domain-specific exceptions by adding error message to flash attributes and redirecting to dashboard.
     */
    @ExceptionHandler({ UserException.class, RefreshTokenException.class, OtpException.class })
    public String handleDomainExceptions(RuntimeException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard/home";
    }
}