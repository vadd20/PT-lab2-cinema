package packages.users.Clients;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.TableHall;
import packages.DB.TableSession;
import packages.DB.users.TableClientImpl;
import packages.Entering;
import packages.MyUtils;
import packages.objects.Hall;
import packages.objects.Session;

import java.sql.SQLException;
import java.util.Scanner;

@Component
@Getter
public class FriendClient implements Client {

    private ApplicationContext applicationContext;

    @Autowired
    public FriendClient(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Setter private int id;
    private String login;
    private String password;
    private String name;
    private String number;
    private String email;
    private int budget;
    private String status;
    private int tickets;

    @Override
    public void createClientFromDb(int id, String login, String password, String name, String number, String email, int budget, int tickets) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.number = number;
        this.email = email;
        this.budget = budget;
        this.status = "friend";
        this.tickets = tickets;
    }

    @Override
    public void chooseAvailableSessions() throws SQLException {
        TableSession tableSession = applicationContext.getBean(TableSession.class);
        tableSession.showAvailableSessions(status);
    }

    @Override
    public int chooseSession() {
        return MyUtils.getSessionId();
    }

    @Override
    public void buyTicket(int sessionId) throws SQLException {
        TableHall tableHall = applicationContext.getBean(TableHall.class);
        TableSession tableSession = applicationContext.getBean(TableSession.class);

        Scanner scanner = new Scanner(System.in);
        Hall hall = tableHall.getHallData(sessionId);

        Session session = tableSession.getSessionData(sessionId);

        while (true) {
            if (!(hall.checkAvailability(this.budget) && session.checkAvailability())) {
                break;
            }

            int row = scanner.nextInt();
            int column = scanner.nextInt();
            String placeStatus = session.getPlaces().get(row - 1).get(column - 1);

            if (placeStatus.equals("-")) {
                session.reservePlace(row - 1, column - 1);

                tableSession.reservePlaceInDb(hall.getId(), row, column);

                this.budget -= hall.getPrice() * 0.9;
                this.tickets += 1;

                TableClientImpl tableClient = applicationContext.getBean(TableClientImpl.class);
                tableClient.reduceBudgetAndIncreaseTickets(this.id, this.budget, this.tickets);
                System.out.println("вы купили билет. ваш бюджет " + this.budget);

                if (this.tickets > 7) {
                    System.out.println("поздравляем. вы теперь VIP клиент. Вам скидка 20% и кофе в подарок");

                    this.status = "VIP";
                    tableClient.changeStatus(this.id, this.status);
                    System.out.println("если хотите еще. введите yes");
                    String wantToBuy = scanner.next();

                    if (wantToBuy.equals("yes")) {
                        Entering entering = applicationContext.getBean(Entering.class);
                        entering.chooseClientStatus(tableClient, this.id);
                    }
                    break;
                }

                System.out.println("если хотите еще. введите yes");
                String wantToBuy = scanner.next();
                if (wantToBuy.equals("yes")) {
                    continue;
                }

                break;
            } else {
                System.out.println("данное место занято. попробуйте еще раз");
            }
        }
    }
}

