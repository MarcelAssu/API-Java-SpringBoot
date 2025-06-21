package br.edu.atitus.api_sample.components;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.atitus.api_sample.services.UserServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final UserServices userServices;

    public AuthTokenFilter(UserServices userServices) {
        super();
        this.userServices = userServices;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = JWTUtils.getJwtFromRequest(request);

        if (jwt != null) {
            logger.info("JWT encontrado na requisição.");

            String email = JWTUtils.validateToken(jwt);

            if (email != null) {
                logger.info("Email extraído do JWT: {}", email);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userServices.loadUserByUsername(email);

                    if (user != null) {
                        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
                        logger.info("Usuário carregado: {}. Autoridades: {}", user.getUsername(), authorities);

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        logger.info("Usuário autenticado no SecurityContextHolder.");
                    } else {
                        logger.warn("loadUserByUsername retornou nulo para o email: {}", email);
                    }
                } else {
                     logger.info("Usuário já autenticado no SecurityContextHolder. Pulando processamento do JWT.");
                }
            } else {
                logger.warn("Falha ao validar JWT ou extrair email. Token pode ser inválido/expirado ou malformado.");
            }
        } else {
            logger.debug("Nenhum JWT encontrado na requisição.");
        }
        filterChain.doFilter(request, response);
    }
}