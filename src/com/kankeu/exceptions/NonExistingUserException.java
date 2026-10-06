package com.kankeu.exceptions;

public class NonExistingUserException extends Exception
{
    public NonExistingUserException(String message)
    {
        super(message);
    }
}
