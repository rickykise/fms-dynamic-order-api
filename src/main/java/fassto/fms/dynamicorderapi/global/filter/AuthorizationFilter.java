package fassto.fms.dynamicorderapi.global.filter;

import fassto.fms.dynamicorderapi.global.config.SecurityProperties;
import fassto.fms.dynamicorderapi.global.response.CommonResponse;
import fassto.fms.dynamicorderapi.global.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private final SecurityProperties securityProperties;

    public AuthorizationFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authorizationHeader = httpRequest.getHeader(securityProperties.getHeader());

        // Authorization 헤더 값 검증
        if (authorizationHeader == null || !authorizationHeader.equals(securityProperties.getExpectedValue())) {
            this.handleUnauthorizedResponse(httpResponse);
            return;
        }

        // 헤더가 올바르다면 요청 처리 계속 진행
        chain.doFilter(request, response);
    }

    private void handleUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        ResponseEntity<CommonResponse<Object>> errorResponse = CommonResponse.fail(ErrorCode.UNAUTHORIZED.getMessage());

        // JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
