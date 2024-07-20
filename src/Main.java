import java.text.MessageFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ConsoleService serv = new ConsoleService();
        serv.start();
    }

    public static class Product implements Comparable<Product> {
        private String productName;
        private double price;

        public int compareTo(Product product) {
            return (int)Math.signum(price - product.price);
        }

        public Product(String productName, double price)
        {
            this.price = price;
            this.productName = productName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Product product = (Product) o;
            return Double.compare(price, product.price) == 0 && Objects.equals(productName, product.productName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productName, price);
        }
    }

    public static class Human implements Buyer {

        private String firstName;
        private String lastName;
        private double money;
        private Set<Product> products = new HashSet<>();

        public Human(String firstName, String lastName, double money)
        {
            this.firstName = firstName;
            this.lastName = lastName;
            this.money = money;
        }

        public void buyProduct(Product product, Shop shop) {
            try {
                shop.sellProduct(product, this);
                products.add(product);
                money -= product.price;
            }
            catch (SellProductException e)
            {
                System.out.println(e.getMessage());
            }
        }

    }


    public interface Buyer {
        void buyProduct(Product product, Shop shop);
    }


    public static class Shop {
        private String name;
        private double money;
        private Map<Product,Integer> products = new HashMap<Product, Integer>();

        public Shop(String name, double money)
        {
            this.name = name;
            this.money = money;
        }

        public void AddProduct(Product product, int amount)
        {
            products.put(product, amount);
        }


        public void sellProduct(Product product, Human human) throws SellProductException {
            if (products.get(product) == 0)
                throw new SellProductException((
                        "Продукта с именем {0} нет в наличии"
                ));
            if (human.money < product.price) {
                throw new SellProductException(("Уважаемый {0} {1}, для покупки товара недостаточно средств"
                ));
            }
            money += product.price - calculateNds(product.price);
            System.out.printf("%s, вы успешно совершили покупку! C уважением, %s", human.firstName, name);
        }


        private double calculateNds(double price) {
            return price * 0.13;
        }


        public List<Product> printAndGetAllProductsWithCount() {
            int i = 1;
            ArrayList<Product> list = new ArrayList<>();
            for (var entry : products.entrySet())
            {
                list.add(entry.getKey());
                System.out.printf("%d. %s - %d - %.2f\n", i++, entry.getKey().productName, entry.getValue(), entry.getKey().price);
            }
            return list;
        }

    }



    public static class SellProductException extends Exception {
        public SellProductException(String msg)
        {
            super(msg);
        }
    }


    public static class ConsoleService {
        private Shop shop;
        private Human human;

        public void start() {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            String[] inputs = input.split(" ");
            int productNum = Integer.parseInt(sc.nextLine());
            shop = new Shop(inputs[0], Double.parseDouble(inputs[1]));
            for (int i = 0; i < productNum; i++)
            {
                inputs = sc.nextLine().split(" ");
                shop.AddProduct(
                        new Product(inputs[0], Double.parseDouble(inputs[1])),
                        Integer.parseInt(inputs[2])
                );
            }
            inputs = sc.nextLine().split(" ");
            human = new Human(inputs[0], inputs[1], Double.parseDouble(inputs[2]));
            System.out.print("""     
            1. Посмотреть список товаров
            2. Выход
            """);
            input = sc.nextLine();
            if (input.equals("2")) return;
            var products = shop.printAndGetAllProductsWithCount();
            input = sc.nextLine();
            human.buyProduct(products.get(Integer.parseInt(input) - 1), shop);
        }
    }
}