package edu.ncsu.csc326.wolfcafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;
import edu.ncsu.csc326.wolfcafe.entity.User;

/**
 * Repository interface for users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Returns the user object by user name
	 * @param username user's username
	 * @return User object or exception on error
	 */
    Optional<User> findByUsername(String username);

    /**
     * Returns true if a user exists with the given email.
     * @param email email to search
     * @return true if email exists for a user
     */
    Boolean existsByEmail(String email);

    /**
     * Returns the user object associated with either the username or email
     * @param username user's username
     * @param email user's email
     * @return User object or exception on error
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Returns true if a user exists with the given username.
     * @param username username to search
     * @return true if username exists for a user
     */
    Boolean existsByUsername(String username);
    
    /**
     * Lists all the users with the same role
     * @param role the defined role 
     * @return all users with the defined role
     */
    List<User> findByRolesContaining(Role role);

    /**
     * Deletes the user by id
     * @param id
     */
	void deleteUserById(Long id);

	/**
	 * Deletes the user by username
	 * @param username
	 */
	void deleteUserByUsername(String username);
}

