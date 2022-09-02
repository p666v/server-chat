package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final ServerService serverService;
    private final UserDao userDao;
    private User user;





    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connection");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String messageFromClient;

        while ((messageFromClient = bufferedReader.readLine()) != null) {
            if (messageFromClient.contains("Registration")) {
                registration(messageFromClient);
            } else if (messageFromClient.contains("autho")) {
                if (authorization(messageFromClient)) {
                    serverService.addObserver(this);
                    serverService.notifyObserverExpectMe(user.getName() + " присоеденился к чату", this);
                    while ((messageFromClient = bufferedReader.readLine()) != null) {
                        if (!messageFromClient.equals("!Exit!")) {
                            System.out.println(user.getName() + ":" + messageFromClient);
                            serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);
                        } else {
                            serverService.notifyObserverExpectMe(user.getName() + " покинул чат", this);
                            serverService.deleteObserver(this);
                            bufferedReader.close();
                            socket.close();
                        }
                    }
                }
            } else if (messageFromClient.equals("!Exit!")) {
                bufferedReader.close();
                socket.close();
            }
        }
    }

//        if (authorization(bufferedReader)) {
//            serverService.addObserver(this);
//            while ((messageFromClient = bufferedReader.readLine()) != null) {
//                System.out.println(user.getName() + ":" + messageFromClient);
//                serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);
//            }
//        }


    @SneakyThrows
    private boolean authorization(String messageFromClient) {
        if (messageFromClient.startsWith("!autho!")) {
            String login = messageFromClient.substring(7).split(":")[0];
            String password = messageFromClient.substring(7).split(":")[1];
            user = userDao.findByNameAndPassword(login, password);

            return true;
        }
        return false;
    }

    @SneakyThrows
    private void registration(String messageFromClient) {
        if (messageFromClient.startsWith("!Registration!")) {
            String login = messageFromClient.substring(14).split(":")[0];
            String password = messageFromClient.substring(14).split(":")[1];
            userDao.createUser(login, password);

        }
    }

    @SneakyThrows
    @Override
    public void notifyMe(String message) {
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());
        clientWriter.println(message);
        clientWriter.flush();
    }


//    @SneakyThrows
//    private boolean authorization(BufferedReader bufferedReader) {
//        String authorizationMessage;
//        while ((authorizationMessage = bufferedReader.readLine()) != null) {
//            if (authorizationMessage.startsWith("!autho!")) {
//                String login = authorizationMessage.substring(7).split(":")[0];
//                String password = authorizationMessage.substring(7).split(":")[1];
//                user = userDao.findByNameAndPassword(login, password);
//                return true;
//            }
//        }
//        return false;
//    }

//    @SneakyThrows
//    private void registration(BufferedReader bufferedReader) {
//        String registrationMessage;
//        while ((registrationMessage = bufferedReader.readLine()) != null) {
//            if (registrationMessage.startsWith("!Registration!")) {
//                String login = registrationMessage.substring(14).split(":")[0];
//                String password = registrationMessage.substring(14).split(":")[1];
//                userDao.createUser(login, password);
//            }
//        }
//    }


}
