package com.ksj.eoisa.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ksj.eoisa.dto.SignDTO;
import com.ksj.eoisa.dto.CustomUserDetailsDTO;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private SqlSession sqlSession;

	boolean enabled = true;
	boolean accountNonExpired = true;
	boolean credentialsNonExpired = true;
	boolean accountNonLocked = true;

	public CustomUserDetailsService() {}

	public CustomUserDetailsService(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DisabledException {
		SignDTO userDetails = sqlSession.selectOne("com.ksj.eoisa.dto.SignDTO.userdetails", username);
        
        if(userDetails == null) throw new UsernameNotFoundException(username);
		if(userDetails.getEnabled() == 0) throw new DisabledException(username);
        
        List<GrantedAuthority> gas = new ArrayList<GrantedAuthority>();
		gas.add(new SimpleGrantedAuthority(userDetails.getAuthority()));
		
		return new CustomUserDetailsDTO(
			userDetails.getUsername(),
            userDetails.getPassword(),
            userDetails.getNickname(),
			userDetails.getProfile_pic(),
			userDetails.getPlatform(),
			enabled,
			accountNonExpired,
			credentialsNonExpired,
			accountNonLocked,
			gas
		);
	}
	
}