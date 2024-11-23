package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.JwtAuthResponse;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.User;

/**
 * Authorization service
 */
public interface AuthService {

    /**
     * Lets admin add a staff to the system
     *
     * @param name
     *            name of the staff
     * @param username
     *            username of the staff
     * @param email
     *            email of the staff
     * @return message for success or failure
     *
     * @author Diya Bhavsar
     */
    String addStaff ( final String name, final String username, final String email );

    /**
     *
     * Lets admin add a marketer to the system
     *
     * @param name
     *            The name of the marketer
     * @param username
     *            The username of the marketer
     * @param email
     *            The email of the marketer
     * @return A message indicating success or failure
     */
    String addMarketer ( final String name, final String username, final String email );

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    // /**
    // * Deletes the given user by id
    // *
    // * @param id
    // * id of user to delete
    // */
    // void deleteUserById ( Long id );

    // /**
    // * Deletes the given user by id
    // * @param id id of user to delete
    // */
    // void deleteUserById(Long id);

    /**
     * Checks if the currently authenticated user has ADMIN or STAFF roles.
     *
     * @return true if the user is an admin or staff, false otherwise
     */
    public boolean isUserAdminOrStaff ();

    public boolean isUserAdmin ();

    /**
     * Gets all the users of the system.
     *
     * @return users
     */
    List<User> getAllUsers ();

    /**
     * Gets all the customers of the system
     *
     * @return customers
     */
    List<User> getAllCustomers ();

    /**
     * Gets all the staff users of the system
     *
     * @return staff
     */
    List<User> getAllStaff ();

    /**
     * Gets all the marketers in the system
     *
     * @return Marketers
     */
    List<User> getAllMarketers ();

    /**
     * Edits the existing user
     *
     * @param existingUsername
     *            existing username
     * @param newUsername
     *            new username
     * @param newName
     *            new name
     * @param newEmail
     *            new email address
     * @param newPassword
     *            new password
     * @return failure or success
     */
    public String editUser ( String existingUsername, String newUsername, String newName, String newEmail,
            String newPassword );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserByUsername ( String username );
}
