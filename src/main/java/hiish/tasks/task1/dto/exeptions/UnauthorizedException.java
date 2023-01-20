package hiish.tasks.task1.dto.exeptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Login or passord wrong")
public class UnauthorizedException extends RuntimeException {
}
