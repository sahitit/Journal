package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Value ( "${app.admin-user-password}" )
    private String  adminUserPassword;

    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    public void testAddStaff_AsAdmin () throws Exception {
        final RegisterDto staffRegistrationDto = new RegisterDto( "New Staff", "newstaff", "newstaff@example.com",
                "password123" );

        mvc.perform( post( "/api/auth/addStaff" ).with( user( "admin" ).roles( "ADMIN" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( staffRegistrationDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() )
                .andExpect( content()
                        .string( "Staff member added successfully. They can now complete their registration." ) );
    }

    @Test
    @Transactional
    public void testAddMarketer_AsAdmin () throws Exception {
        final RegisterDto marketerRegistrationDto = new RegisterDto( "New Marketer", "newMarketer",
                "newMarketer@example.com", "password123" );

        mvc.perform( post( "/api/auth/addMarketer" ).with( user( "admin" ).roles( "ADMIN" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( marketerRegistrationDto ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() ).andExpect(
                        content().string( "Marketer added successfully. They can now complete their registration." ) );
    }

    @Test
    @Transactional
    public void testGetAllUsers_AsAdmin () throws Exception {
        mvc.perform(
                get( "/api/auth/users" ).with( user( "admin" ).roles( "ADMIN" ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$" ).isArray() );
    }

    @Test
    @Transactional
    public void testGetAllCustomers_AsAdmin () throws Exception {
        mvc.perform( get( "/api/auth/customers" ).with( user( "admin" ).roles( "ADMIN" ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$" ).isArray() );
    }

    @Test
    @Transactional
    public void testGetAllStaff_AsAdmin () throws Exception {
        mvc.perform(
                get( "/api/auth/staff" ).with( user( "admin" ).roles( "ADMIN" ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$" ).isArray() );
    }

    @Test
    @Transactional
    public void testGetAllMarketers_AsAdmin () throws Exception {
        mvc.perform( get( "/api/auth/marketers" ).with( user( "admin" ).roles( "ADMIN" ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$" ).isArray() );
    }

    @Test
    @Transactional
    public void testCreateCustomerAndLogin () throws Exception {
        final RegisterDto registerDto = new RegisterDto( "Jordan Estes", "jestes", "vitae.erat@yahoo.edu",
                "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( registerDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andExpect( content().string( "User registered successfully." ) );

        final LoginDto loginDto = new LoginDto( "jestes", "JXB16TBD4LC" );

        mvc.perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( loginDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.tokenType" ).value( "Bearer" ) )
                .andExpect( jsonPath( "$.role" ).value( "ROLE_CUSTOMER" ) );
    }

    // Test for isAdminOrStaff endpoint
    @Test
    @Transactional
    public void testIsAdminOrStaff_AsAdmin () throws Exception {
        mvc.perform( get( "/api/auth/isAdminOrStaff" ).with( user( "admin" ).roles( "ADMIN" ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( content().string( "User is authorized as staff or admin." ) );
    }

    @Test
    @Transactional
    public void testIsAdminOrStaff_AsStaff () throws Exception {
        mvc.perform( get( "/api/auth/isAdminOrStaff" ).with( user( "staffUser" ).roles( "STAFF" ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( content().string( "User is authorized as staff or admin." ) );
    }

    @Test
    @Transactional
    public void testIsAdminOrStaff_AsCustomer_ShouldFail () throws Exception {
        mvc.perform( get( "/api/auth/isAdminOrStaff" ).with( user( "customerUser" ).roles( "CUSTOMER" ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isForbidden() )
                .andExpect( content().string( "User is not authorized." ) );
    }

}
