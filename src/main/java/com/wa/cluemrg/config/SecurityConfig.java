package com.wa.cluemrg.config;

import com.wa.cluemrg.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] level1List = new String[]{"/","/clue/**","/phone/**","analyze/**","/caseGraph.html","/relationGraph.html","/callLog/**"};
        http.authorizeRequests()
                //.antMatchers("/alarmReceipt/**").permitAll() // 需要匿名访问的路径
                //.antMatchers(level1List).hasRole("level1") // 需要权限的路径
                //.anyRequest().hasAnyRole("LEVEL1")
                //.anyRequest().anonymous()
                .antMatchers("/user/login","/css/**", "/js/**", "/old/**").permitAll() // 允许无需权限访问的静态资源
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html") // 登录页面
                .loginProcessingUrl("/user/login") //登录访问路径
                .defaultSuccessUrl("/") // 登录成功后的默认跳转页面
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout") // 注销URL
                .logoutSuccessUrl("/user/login") // 注销成功后重定向的URL
                .invalidateHttpSession(true) // 使HttpSession失效
                .deleteCookies("JSESSIONID"); // 删除Cookies（根据需要删除）
        http.csrf().disable();
    }

    //认证   springboot 2.1.X 可以直接使用
    //密码编码： passwordEncoder
    //在spring Security 5.0+ 新增了很多的加密方法
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这些数据正常应该从数据库中读取  现在测试的是从内存中读取的数据
        /*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())   //加了一个密码的编码规则
                .withUser("xwzd").password(new BCryptPasswordEncoder().encode("123456")).roles("LEVEL1","LEVEL2","LEVEL3")
                .and()
                .withUser("root").password(new BCryptPasswordEncoder().encode("123456")).roles("LEVEL1","LEVEL2","LEVEL3")
                .and()
                .withUser("guest").password(new BCryptPasswordEncoder().encode("123456")).roles("LEVEL1");
*/
        //从数据库中获取数据
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .withDefaultSchema()
//                .withUser(users.username("user").password("password").roles("USER"))
//                .withUser(users.username("admin").password("password").roles("USER","ADMIN"));
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }


    /*@Override
    public void configure(WebSecurity web) throws Exception {
        //String[] publicList = new String[]{"/alarmReceipt/**","/attribution/**","/bsLocation/**","/case/**","/effect/**","/graph/**","/nodeTag/**","/phoneImei/**","/phoneImsi/**","/phoneTag/**","/qgBtClue/**","/qgDkClue/**","/ttClue/**","/victim/**","/btClue/**"};
        String[] publicList = new String[]{"**.css","**.js"};
        web.ignoring().antMatchers(publicList);
    }*/


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
