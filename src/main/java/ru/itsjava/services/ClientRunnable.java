package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.dao.MessageDao;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.Message;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final ServerService serverService;
    private final UserDao userDao;
    private final MessageDao messageDao;
    private User user;
    private List<Message> messageList = new ArrayList<>();

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
                    serverService.notifyObserverMe("Авторизация успешна!", this); //test
                    serverService.notifyObserverExpectMe(user.getName() + " присоеденился к чату", this);
                    while ((messageFromClient = bufferedReader.readLine()) != null) {
                        if (!messageFromClient.equals("!Exit!")) {
                            System.out.println(user.getName() + ":" + messageFromClient);
                            Message message = new Message(user.getName(), messageFromClient, user.getId());
                            messageList.add(message);
                            serverService.notifyObserverExpectMe(user.getName() + ":" + messageFromClient, this);

                        } else {
                            for (int i = 0; i < messageList.size(); i++) {
                                messageDao.writeMessage(
                                        messageList.get(i).getUser(),
                                        messageList.get(i).getText(),
                                        messageList.get(i).getId());
                            }
                            messageList.clear();
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


    @SneakyThrows
    private boolean authorization(String messageFromClient) {
        if (messageFromClient.startsWith("!autho!")) {
            String login = messageFromClient.substring(7).split(":")[0];
            String password = messageFromClient.substring(7).split(":")[1];
            user = userDao.findByNameAndPassword(login, password);
            System.out.println("Пользователь " + user.getName() + ": Авторизация успешна!");
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

}
