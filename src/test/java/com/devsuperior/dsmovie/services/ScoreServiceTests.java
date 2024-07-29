package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	MovieEntity movie;
	MovieDTO movieDTO;
	
	ScoreEntity score;
	ScoreDTO scoreDTO;
	
	UserEntity user;
	
	Long existingMovieId;
	Long nonExistingMovieId;
	
	@BeforeEach
	void setUp() throws Exception {
		
		movie = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		
		score = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		
		user = UserFactory.createUserEntity();
		
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(scoreRepository.saveAndFlush(score)).thenReturn(score);
		Mockito.when(movieRepository.save(movie)).thenReturn(movie);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		
		MovieDTO result = service.saveScore(scoreDTO);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.saveScore(scoreDTO);
		});
	}
}
