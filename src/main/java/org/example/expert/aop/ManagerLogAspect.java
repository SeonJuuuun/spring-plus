package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerLogAspect {

    private final LogService logService;

    @Pointcut("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void saveManagerPointcut() {
    }

    @Around("saveManagerPointcut()")
    public Object logManagerRegistration(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        AuthUser authUser = (AuthUser) args[0];
        final Long todoId = (Long) args[1];
        ManagerSaveRequest request = (ManagerSaveRequest) args[2];

        final String email = authUser.getEmail();
        final String logDetails = String.format(
                "%s님이 %d번 id를 가진 유저를 %d번 일정의 담당자로 등록하였습니다.",
                email, request.getManagerUserId(), todoId
        );

        try {
            Object result = joinPoint.proceed();
            logService.saveLog("SUCCESS", logDetails);
            return result;
        } catch (Exception e) {
            logService.saveLog("FAIL", logDetails + ". Error: " + e.getMessage());
            throw e;
        }
    }
}
