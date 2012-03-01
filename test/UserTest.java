import models.User;

import org.junit.Test;

import play.db.jpa.JPA;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class UserTest {
	
	public User createUser() {
		User user = new User();
		user.username = "Leonard";
		user.password = "secret";
		user.fullname = "Leonard Punt";
		user.email = "leonard@test.nl";
		return user;
	}
	
	@Test
	public void createUserTest() {
		running(fakeApplication(), new Runnable() {		
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() throws Throwable {
						User user = createUser();
						User.create(user);
						assertThat(User.all().size()).isEqualTo(1);
						
						User savedUser = User.all().get(0);
						assertThat(user.username).isEqualTo(savedUser.username);
					}
				});				
			}
		});
	}

}
