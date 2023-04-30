package packages.users.Clients;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.TableHall;
import packages.DB.TableSession;
import packages.DB.users.TableClientImpl;
import packages.MyUtils;
import packages.objects.Hall;

import java.sql.SQLException;
import java.util.Scanner;

@Component
@Getter
public class VIPClient implements Client {

    private ApplicationContext applicationContext;

    @Autowired
    public VIPClient (ApplicationContext applicationContext) {
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
        this.status = "VIP";
        this.tickets = tickets;
    }

    @Override
    public void chooseAvailableSessions () throws SQLException {
        TableSession tableSession = applicationContext.getBean(TableSession.class);
        tableSession.showAvailableSessions(status);
    }


    @Override
    public void chooseSession() throws SQLException {
        MyUtils myUtils = applicationContext.getBean(MyUtils.class);
        myUtils.getAndSetSessionData();
    }

    @Override
    public void buyTicket() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Hall hall = applicationContext.getBean(Hall.class);

        while (true) {
            if (!hall.checkAvailability(this.budget)) {
                break;
            }

            int row = scanner.nextInt();
            int column = scanner.nextInt();
            String placeStatus = hall.getPlaces().get(row - 1).get(column - 1);

            if (placeStatus.equals("-")) {
                hall.reservePlace(row - 1, column - 1);

                TableHall tableHall = applicationContext.getBean(TableHall.class);
                tableHall.reservePlaceInDb(hall.getId(), row, column);

                this.budget -= hall.getPrice() * 0.8;
                this.tickets += 1;

                TableClientImpl tableClient = applicationContext.getBean(TableClientImpl.class);
                tableClient.reduceBudgetAndIncreaseTickets(this.id, this.budget, this.tickets);
                System.out.println("вы купили билет. Заберите кофе в подарок. Ваш бюджет " + this.budget);

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
