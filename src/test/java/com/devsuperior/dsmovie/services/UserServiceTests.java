package com.devsuperior.dsmovie.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private UserEntity user;
	private String userName;
	private String nonExistinfUserName;

	private List<UserDetailsProjection> userClientList;
	private List<UserDetailsProjection> userAdminList;
	private List<UserDetailsProjection> userAdminUserList;

	@BeforeEach
	void setUp() throws Exception {

		user = UserFactory.createUserEntity();
		userName = user.getName();

		userClientList = UserDetailsFactory.createCustomClientUser(userName);
		userAdminList = UserDetailsFactory.createCustomAdminUser(userName);
		userAdminUserList = UserDetailsFactory.createCustomAdminClientUser(userName);

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(userName);
		Mockito.when(repository.findByUsername(user.getName())).thenReturn(Optional.of(user));

		Mockito.when(repository.searchUserAndRolesByUsername(userName)).thenReturn(userClientList);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistinfUserName)).thenReturn(new ArrayList<>());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getName(), userName);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		UserDetails result = service.loadUserByUsername(userName);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), userName);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistinfUserName);
		});
	}
}
