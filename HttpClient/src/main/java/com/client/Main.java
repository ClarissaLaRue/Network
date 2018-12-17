package com.client;

import com.client.Client;

import java.io.IOException;

public class Main {
    public static void main(String args[]){
        try {
            new Client().start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
