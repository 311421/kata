import java.text.MessageFormat;

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
            return Double.compare(price, product.getPrice()) == 0 && java.util.Objects.equals(productName, product.getProductName());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(productName, price);
        }

        public double getPrice() {
            return price;
        }

        public String getProductName() {
            return productName;
        }
    }

    public static class Human implements Buyer {

        private String firstName;
        private String lastName;
        private double money;
        private java.util.Set<Product> products = new java.util.HashSet<>();

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
                money -= product.getPrice();
            }
            catch (SellProductException e)
            {
                System.out.println(e.getMessage());
            }
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public double getMoney() {
            return money;
        }


    }


    public interface Buyer {
        void buyProduct(Product product, Shop shop);
    }


    public static class Shop {
        private String name;
        private double money;
        private java.util.Map<Product,Integer> products = new java.util.HashMap<Product, Integer>();

        public Shop(String name, double money)
        {
            this.name = name;
            this.money = money;
        }

        public void addProduct(Product product, int amount)
        {
            products.put(product, amount);
        }


        public void sellProduct(Product product, Human human) throws SellProductException {
            if (products.get(product) == 0)
                throw new SellProductException((
                        "Продукта с именем {0} нет в наличии"
                ));
            if (human.getMoney() < product.getPrice()) {
                throw new SellProductException(("Уважаемый {0} {1}, для покупки товара недостаточно средств"
                ));
            }
            money += product.getPrice() - calculateNds(product.getPrice());
            System.out.printf("%s, вы успешно совершили покупку! C уважением, %s", human.getFirstName(), name);
        }


        private double calculateNds(double price) {
            return price * 0.13;
        }


        public java.util.List<Product> printAndGetAllProductsWithCount() {
            int i = 1;
            java.util.ArrayList<Product> list = new java.util.ArrayList<>();
            for (var entry : products.entrySet())
            {
                list.add(entry.getKey());
                System.out.printf("%d. %s - %d - %.2f\n", i++, entry.getKey().getProductName(), entry.getValue(), entry.getKey().getPrice());
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
            java.util.Scanner sc = new java.util.Scanner(System.in);
            String input = sc.nextLine();
            String[] inputs = input.split(" ");
            int productNum = Integer.parseInt(sc.nextLine());
            shop = new Shop(inputs[0], Double.parseDouble(inputs[1]));
            for (int i = 0; i < productNum; i++)
            {
                inputs = sc.nextLine().split(" ");
                shop.addProduct(
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