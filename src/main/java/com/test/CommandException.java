package com.test;

public class CommandException extends Exception
{
    private static final long serialVersionUID = 168975389153471274L;

    public CommandException(String message)
    {
        super(message);
    }

    public CommandException(Throwable cause)
    {
        super(cause);
    }

    public CommandException(String message, Throwable cause)
    {
        super(message, cause);
    }
}