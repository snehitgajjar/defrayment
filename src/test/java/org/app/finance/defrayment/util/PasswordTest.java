package org.app.finance.defrayment.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Password.class, SecretKeyFactory.class})
public class PasswordTest {

	byte[] salt = new byte[16];
	byte[] hashedPass;
	char[] charPass = "TestPassword".toCharArray();
	SecureRandom mockRandom = null;
	Password password = null;
	SecretKeyFactory secretKeyFactory;
	PBEKeySpec pbeKey;
	
	@Before
	public void setUp() throws Exception{
		hashedPass = "HashedPassword".getBytes();
		
		mockRandom = Mockito.mock(SecureRandom.class);
		
		Mockito.doNothing().when(mockRandom).nextBytes(salt);
		
		password = new Password(mockRandom);
		
		secretKeyFactory = PowerMockito.mock(SecretKeyFactory.class);	
		pbeKey = PowerMockito.mock(PBEKeySpec.class);
		SecretKey secretKey = PowerMockito.mock(SecretKey.class);
		PowerMockito.mockStatic(SecretKeyFactory.class);	
		
		PowerMockito.whenNew(PBEKeySpec.class).withArguments(Mockito.any(char[].class), Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt()).thenReturn(pbeKey);
		Mockito.when(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")).thenReturn(secretKeyFactory);		
		PowerMockito.when(secretKeyFactory.generateSecret(pbeKey)).thenReturn(secretKey);
		PowerMockito.when(secretKey.getEncoded()).thenReturn(hashedPass);
		PowerMockito.when(password.hash(charPass, salt)).thenReturn(hashedPass);
	}
	
	@Test
	public void getNextSaltTestByteArray() throws Exception{		
		byte[] actual = password.getNextSalt();
		
		assertEquals(actual.getClass(),byte[].class);	
	}
	
	@Test
	public void getNextSaltTestLength() throws Exception{		
		byte[] actual = password.getNextSalt();
		
		assertEquals(actual.length, salt.length);
 		
	}
	
	@Test
	public void hashSuccessfulTest() throws Exception{	
		byte[] actual = password.hash("TestPassword".toCharArray(), salt);
		byte[] expected = hashedPass;
		
		assertEquals(expected, actual);
	}
	
	@Test(expected=AssertionError.class)
	public void hashTestNoSuchAlgorithmException() throws Exception{
		Mockito.when(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")).thenThrow(new NoSuchAlgorithmException());
		
		password.hash("TestPassword".toCharArray(), salt);
	}
	
	@Test(expected=AssertionError.class)
	public void hashTestInvalidKeySpecException() throws Exception{
		PowerMockito.when(secretKeyFactory.generateSecret(pbeKey)).thenThrow(new InvalidKeySpecException());
		
		password.hash("TestPassword".toCharArray(), salt);
	}
	
	@Test
	public void hashTestFinally() throws Exception{
		password.hash("TestPassword".toCharArray(), salt);
		
		Mockito.verify(pbeKey).clearPassword();
	}
	
	@Test
	public void isExpectedPasswordTestMatchingPassword(){		
		boolean actual = password.isExpectedPassword(charPass, salt, hashedPass);
		
		assertTrue(actual);
	}
	
	@Test
	public void isExpectedPasswordTestWrongPassword(){
		byte[] wrongHashedPass = Arrays.copyOfRange(hashedPass, 0, hashedPass.length - 2);
		
		boolean actual = password.isExpectedPassword(charPass, salt, wrongHashedPass);
		
		assertFalse(actual);
	}
	
	@Test
	public void generateRandomPasswordTestPasswordLength(){		
		int actual = password.generateRandomPassword(15).length();
		
		assertEquals(actual, 15);
	}
	
	@Test
	public void generateRandomPasswordTestZeroLength(){		
		int actual = password.generateRandomPassword(0).length();
		
		assertEquals(actual, 0);
	}
}
