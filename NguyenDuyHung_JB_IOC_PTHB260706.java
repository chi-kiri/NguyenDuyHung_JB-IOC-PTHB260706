import java.sql.*;
import java.util.Scanner;

public class NguyenDuyHung_JB_IOC_PTHB260706 {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/db_cinema_booking";
        String user = "postgres";
        String password = "hung123";

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            while (true) {
                System.out.println("\n1. Xem vé  2. Thêm  3. Sửa  4. Xóa"
                        + "\n5. Tìm khách  6. Tìm phim  7. Thoát");

                int choice = number("Chọn: ");

                switch (choice) {
                    case 1:
                        show(con, "SELECT * FROM get_all_bookings()", null);
                        break;
                    case 2:
                        save(con, false);
                        break;
                    case 3:
                        save(con, true);
                        break;
                    case 4:
                        delete(con);
                        break;
                    case 5:
                        show(con, "SELECT * FROM find_bookings_by_customer(?)",
                                text("Tên khách: "));
                        break;
                    case 6:
                        show(con, "SELECT * FROM find_bookings_by_movie(?)",
                                text("Tên phim: "));
                        break;
                    case 7:
                        return;
                    default:
                        System.out.println("Chọn từ 1 đến 7.");
                }
            }
        }
    }

    static void show(Connection con, String sql, String keyword) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (keyword != null) {
                ps.setString(1, keyword);
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("Mã: " + rs.getInt("booking_id")
                            + " | Phim: " + rs.getString("movie_title")
                            + " | Khách: " + rs.getString("customer_name")
                            + " | Chiếu: " + rs.getTimestamp("show_time")
                            + " | Đặt: " + rs.getTimestamp("booking_date")
                            + " | Ghế: " + rs.getInt("seat_quantity")
                            + " | Trạng thái: " + rs.getString("status"));
                }

                if (!found) {
                    System.out.println("Không có dữ liệu.");
                }
            }
        }
    }

    static void save(Connection con, boolean update) throws Exception {
        int id = 0;

        if (update) {
            id = number("Mã vé cần sửa: ");

            if (!exists(con, id)) {
                System.out.println("Không tìm thấy mã vé.");
                return;
            }

            System.out.println("Nhập lại các thông tin:");
        }

        String movie = text("Tên phim: ");
        String customer = text("Tên khách: ");
        Timestamp showTime = time("Suất chiếu");
        Timestamp bookingDate;

        do {
            bookingDate = time("Ngày đặt");

            if (bookingDate.after(showTime)) {
                System.out.println("Ngày đặt phải trước suất chiếu.");
            }
        } while (bookingDate.after(showTime));

        int seats = number("Số ghế: ");
        String status = status();

        String sql = update
                ? "CALL update_booking(?, ?, ?, ?, ?, ?, ?)"
                : "CALL add_booking(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;

            if (update) {
                ps.setInt(i++, id);
            }

            ps.setString(i++, movie);
            ps.setString(i++, customer);
            ps.setTimestamp(i++, showTime);
            ps.setTimestamp(i++, bookingDate);
            ps.setInt(i++, seats);
            ps.setString(i, status);

            ps.execute();
            System.out.println(update ? "Đã sửa." : "Đã thêm.");
        }
    }

    static void delete(Connection con) throws Exception {
        int id = number("Mã vé cần xóa: ");

        if (!exists(con, id)) {
            System.out.println("Không tìm thấy mã vé.");
            return;
        }

        if (!text("Nhập y để xác nhận xóa: ").equalsIgnoreCase("y")) {
            return;
        }

        try (PreparedStatement ps = con.prepareStatement("CALL delete_booking(?)")) {
            ps.setInt(1, id);
            ps.execute();
            System.out.println("Đã xóa.");
        }
    }

    static boolean exists(Connection con, int id) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT 1 FROM ticket_bookings WHERE booking_id = ?")) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    static String text(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Không được để trống.");
        }
    }

    static int number(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();

            if (value.matches("[0-9]+")) {
                try {
                    int n = Integer.parseInt(value);
                    if (n > 0) {
                        return n;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            System.out.println("Nhập số nguyên lớn hơn 0.");
        }
    }

    static Timestamp time(String prompt) {
        while (true) {
            String value = text(prompt + " (yyyy-MM-dd HH:mm:ss): ");

            try {
                return Timestamp.valueOf(value);
            } catch (IllegalArgumentException e) {
                System.out.println("Ngày giờ không hợp lệ.");
            }
        }
    }

    static String status() {
        while (true) {
            String value = text("Trạng thái (Booked/Cancelled/CheckedIn): ");

            for (String option : new String[]{"Booked", "Cancelled", "CheckedIn"}) {
                if (value.equalsIgnoreCase(option)) {
                    return option;
                }
            }

            System.out.println("Trạng thái không hợp lệ.");
        }
    }
}