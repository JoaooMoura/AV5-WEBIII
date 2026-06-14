package com.autobots.automanager.configuracao;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.autobots.automanager.adaptadores.UserDetailsServiceImpl;
import com.autobots.automanager.filtros.Autenticador;
import com.autobots.automanager.filtros.Autorizador;
import com.autobots.automanager.jwt.ProvedorJwt;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Seguranca extends WebSecurityConfigurerAdapter {
	private final UserDetailsServiceImpl servico;
	private final ProvedorJwt provedorJwt;

	public Seguranca(UserDetailsServiceImpl servico, ProvedorJwt provedorJwt) {
		this.servico = servico;
		this.provedorJwt = provedorJwt;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();
		http.exceptionHandling()
				.authenticationEntryPoint((request, response, exception) -> response
						.sendError(HttpServletResponse.SC_UNAUTHORIZED))
				.accessDeniedHandler((request, response, exception) -> response
						.sendError(HttpServletResponse.SC_FORBIDDEN));
		http.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers("/auth/login").permitAll()
				.antMatchers(HttpMethod.GET, "/servicos/**").hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR")
				.antMatchers("/servicos/**").hasAnyRole("ADMINISTRADOR", "GERENTE")
				.antMatchers(HttpMethod.GET, "/mercadorias/**").hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR")
				.antMatchers("/mercadorias/**").hasAnyRole("ADMINISTRADOR", "GERENTE")
				.antMatchers("/usuarios/**").authenticated()
				.antMatchers("/vendas/**").authenticated()
				.antMatchers("/clientes/**").hasAnyRole("ADMINISTRADOR", "GERENTE", "VENDEDOR")
				.antMatchers("/empresas/**").hasRole("ADMINISTRADOR")
				.antMatchers("/veiculos/**").hasRole("ADMINISTRADOR")
				.anyRequest().hasRole("ADMINISTRADOR");
		http.addFilter(new Autenticador(authenticationManager(), provedorJwt));
		http.addFilter(new Autorizador(authenticationManager(), provedorJwt, servico));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder autenticador) throws Exception {
		autenticador.userDetailsService(servico).passwordEncoder(codificadorSenha());
	}

	@Bean
	public BCryptPasswordEncoder codificadorSenha() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
		fonte.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return fonte;
	}
}
