package com.restaurant.BTL_JAVA.config;
 
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.restaurant.BTL_JAVA.entity.Restaurant;
import com.restaurant.BTL_JAVA.repository.RestaurantRepository;
 
/**
 * Nạp dữ liệu MẪU khi bảng rỗng: 36 nhà hàng tại Hà Nội (4 trang x 9 quán).
 * Tên quán là hư cấu; tên phố/quận có thật; toạ độ chỉ gần đúng theo khu vực, dùng để demo.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
 
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
 
    private final RestaurantRepository repository;
 
    public DataSeeder(RestaurantRepository repository) {
        this.repository = repository;
    }
 
    private record Seed(String name, String cuisine, String address, long min, long max, double rating,
                        double lat, double lng, String hours, String desc, String imageUrl, String phone) {
    }
 
    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll();
        //if (repository.count() > 0) {
        //    return;
        //}
        List<Seed> seeds = List.of(
        new Seed("Phở Gia Truyền Bát Đàn", "Món Việt", "49 P.Bát Đàn, Phố cổ Hà Nội, Hoàn Kiếm", 55_000, 70_000, 4.3, 21.0336, 105.8450, "06:00 - 11:00 && 17:00 - 21:30", "Phở bò nước trong, thịt mềm, hành chần thơm, đông khách buổi sáng.", "/images/pho-gia-truyen-bat-dan.jpg", "0438 286 574"),
        new Seed("Bún Chả Số 1 Hàng Mành", "Món Việt", "1.P Hàng Mành, Phố cổ Hà Nội,Hoàn Kiếm", 70_000, 120_000, 3.0, 21.0322493, 105.8482095, "08:00 - 22:00", "Bún chả nướng than hoa, nem cua bể giòn rụm.", "/images/bun-cha-hang-manh.jpg", "0329 578 797"),
        new Seed("Chả Cá Thăng Long", "Món Việt", "17A P.Chả Cá, Phố cổ Hà Nội, Hoàn Kiếm", 180_000, 350_000, 4.6, 21.0355290, 105.8493770, "09:00 - 23:00", "Chả cá nướng nghệ ăn kèm bún, thì là và mắm tôm.", "/images/cha-ca-thang-long.jpg", "0798 164 721"),
        new Seed("Bún Đậu Hùng Cường", "Món Việt", "P.Trần Quang Diệu, Đống Đa", 20_000, 50_000, 3.6, 21.0155, 105.8229, "10:00 - 22:00", "Mẹt bún đậu chiên giòn, chả cốm, dồi sụn.", "/images/bun-dau-hung-cuong.jpg", "0816 883 883"),
        new Seed("Quán Ốc Nóng Hà Trang", "Hải sản", "1A P.Đinh Liệt, Phố cổ Hà Nội, Hoàn Kiếm", 35_000, 90_000, 4.2, 21.0338, 105.8528, "16:00 - 22:00", "Ốc xào, ốc luộc sả gừng, ngao hấp, hợp ngồi nhóm bạn.", "/images/quan-oc-nong-ha-trang.jpg", "0243 926 0525"),
        new Seed("Lang Biang - Breakfast, Brunch & Specialty Coffee and Matcha", "Cafe & Bánh", "22 Ng.Hàng Hành, Hoàn Kiếm", 105_000, 240_000, 4.8, 21.0306, 105.8492, "07:00 - 18:00", "Một điểm dừng chân tuyệt vời cho tín đồ của matcha và cà phê đặc sản. Mango Matcha Latte rất thơm và thanh mát. Quán có nhiều lựa chọn đồ ăn healthy và chay. Rất hài lòng với chất lượng dịch vụ ở đây.", "/images/lang-biang.jpg", "0559 310 345"),
        new Seed("Sushi Ichi", "Món Nhật", "98 P.Nguyễn Hữu Huân, Hoàn Kiếm", 250_000, 550_000, 4.9, 21.0321, 105.8543, "11:00 - 13:45 && 17:30 - 21:30", "Sushi, sashimi nhập mới mỗi ngày, quầy omakase nhỏ.", "/images/sushi-ichi.jpg", "0942 759 898"),
        new Seed("Colette French Bistro & Wine Bar", "Món Âu", "12 P.Lò Sũ, Hoàn Kiếm", 390_000, 1_180_000, 4.7, 21.0315, 105.8548, "11:30 - 23:00", "Món Pháp thân thiện, rượu vang theo ly, không gian ấm cúng.", "/images/colette-french-bistro&wine-bar.jpg", "0766 253 630"),
        new Seed("Ding Dimsum 3", "Món Hoa", "21 Xóm Hà Hồi, Cửa Nam", 90_000, 300_000, 4.9, 21.0238, 105.8465, "10:30 - 14:00 && 17:30 - 22:00", "Dimsum hấp nóng, vịt quay, mì hoành thánh.", "/images/ding-dimsum.jpg", "0987 155 598"),
        new Seed("Ashima Triệu Việt Vương", "Lẩu", "182 P.Triệu Việt Vương, Hai Bà Trưng", 300_000, 600_000, 4.7, 21.0124, 105.8492, "11:00 - 21:00", "Lẩu nấm thanh đạm với hơn 10 loại nấm tươi.", "/images/ashima.jpg", "024 7300 7318"),
        new Seed("Cơm Văn Phòng - Phở Sáng - Cafe", "Món Việt", "145 Đ.Nguyễn Tuân, Thanh Xuân", 25_000, 55_000, 4.1, 20.9995, 105.8045, "06:30 - 22:00", "Cơm trưa bình dân, suất đầy đặn, giá sinh viên.", "/images/com-van-phong.jpg", "0000 000 000"),
        new Seed("King BBQ Premium - Vincom Bà Triệu", "Món Hàn", "Bà Triệu/191 P.Bùi Thị Xuân, Tầng 5, Hai Bà Trưng", 190_000, 340_000, 4.7, 21.0114, 105.8495, "10:00 - 21:30", "Thịt nướng bàn than, kimchi và banchan ăn thoải mái.", "/images/king-bbq.jpg", "024 6255 0077"),
        new Seed("Đi ăn chay đi", "Món chay", "57 Ng.Chùa Liên Phái, Bạch Mai, Hai Bà Trưng", 50_000, 110_000, 4.1, 21.0054, 105.8498, "06:30 - 21:00", "Món chay thanh tịnh, nguyên liệu hữu cơ.", "/images/di-an-chay-di.jpg", "0983 134 969"),
        new Seed("Vien Dining", "Món Âu", "3 Ng.Bà Triệu, Hai Bà Trưng", 700_000, 2_500_000, 4.6, 21.0094, 105.8491, "11:00 - 14:30 && 18:00 - 22:00", "Thực đơn tasting 7 món, đặt bàn trước.", "/images/vien-dining.jpg", "0388 451 919"),
        new Seed("Bún Ốc Bà Đằng", "Món Việt", "16.P Hàng Bún, Ba Đình", 35_000, 65_000, 4.3, 21.0410, 105.8455, "08:00 - 21:00", "Bún ốc nước dùng chua thanh, chuối xanh, đậu rán nóng.", "/images/bun-oc-ba-dang.jpg", "0924 106 222"),
        new Seed("Lẩu Bò Nhúng Dấm 19 Dốc Hòe Nhai", "Lẩu", "dốc P.Hòe Nhai, Ba Đình", 200_000, 350_000, 3.7, 21.0442, 105.8471, "10:00 - 14:00 && 17:00 - Midnight", "Lẩu bò nhúng dấm, nước dùng ngọt thanh, rau tươi.", "/images/lau-bo-nhung-dam.jpg", "0356 016 771"),
        new Seed("Khu Văn Hóa Ẩm Thực Sen Tây Hồ", "Hải sản", "Đ.Lạc Long Quân/614 Cầu Nhật Tân, Tây Hồ", 400_000, 500_000, 4.2, 21.0748, 105.814, "11:00 - 14:00 && 18:00 - 21:00", "Hải sản tươi sống, chế biến tại chỗ, phòng riêng cho nhóm đông.", "/images/khu-van-hoa-am-thuc-sen-tay-ho.jpg", "1900 6607"),
        new Seed("Cafe View Hồ Tây", "Cafe & Bánh", "Hanoi City, 28 P.Quảng An, Tây Hồ", 45_000, 120_000, 4.6, 21.062, 105.828, "07:00 - 22:00", "Không gian xanh ven hồ, bánh ngọt tự làm, trà hoa quả.", "/images/cafe-view-ho-tay.jpg", "0339 067 911"),
        new Seed("Nhà Hàng Nhật Bản ManMaRu", "Món Nhật", "Đội Cấn/281 P.Liễu Giai, Ngọc Hà", 100_000, 190_000, 4.6, 21.0360, 105.8147, "11:00 - 14:00 && 17:30 - 23:00", "Ramen tonkotsu nước dùng hầm 12 giờ.", "/images/nha-hang-nhat-ban-manmaru.jpg", "0911 141 470"),
        new Seed("Món Cuốn - Nguyên Bản Vị Cuốn Trứ Danh", "Món Việt", "87 Hào Nam, Ô Chợ Dừa", 90_000, 110_000, 5.0, 21.0238, 105.8266, "10:00 - 22:00", "Phở cuốn tươi, chấm nước mắm chua ngọt, thêm nem rán.", "/images/mon-cuon.jpg", "0962 916 803"),
        new Seed("Pizza Napoli", "Món Âu", "Tòa CT1B, HomeLand, Bồ Đề", 130_000, 290_000, 4.7, 21.0558, 105.8832, "10:00 - 21:00", "Pizza lò đá kiểu Napoli, mì Ý sốt kem.", "/images/pizza-napoli.jpg", "0909 650 994"),
        new Seed("Cơm Niêu Hoa Lư", "Món Việt", "90 Hào Nam, Ô Chợ Dừa", 100_000, 160_000, 4.2, 21.0241, 105.8268, "09:00 - 14:00 && 17:00 - 21:30", "Cơm niêu cháy giòn, các món kho tộ đậm vị.", "/images/com-nieu-hoa-lu.jpg", "0866 906 905"),
        new Seed("Kỷ Nguyên Bò Sốt - Bít Tết, Bò Né", "Món Việt", "66 Hồ Văn Quán, KĐT Văn Quán, Hà Đông", 35_000, 80_000, 4.1, 20.9781, 105.7927, "10:00 - 14:00 && 17:00 - 21:30", "Bò né chảo gang, pate, trứng ốp la.", "/images/ky-nguyen-bo-sot-bit-tet-bo-ne.jpg", "0345 232 434"),
        new Seed("L'amour Steak", "Món Âu", "Đường 19/5 Tầng 1 Tòa New Skyline Văn Quán, Hà Đông", 420_000, 950_000, 4.5, 20.9765, 105.7899, "09:00 - 22:00", "Bít tết bò Úc nhập khẩu, hầm rượu vang.", "/images/l'amour-steak.jpg", "0835 333 555"),
        new Seed("Quán Hải Sản Biển Đông 3", "Hải sản", "P.Bằng Liệt, KĐT Tây Nam Linh Đàm, Hoàng Mai", 220_000, 450_000, 3.8, 20.9632, 105.8245, "09:00 - 23:00", "Hải sản bình dân, tôm cua theo cân, nhà hàng rộng.", "/images/quan-hai-san-bien-dong.jpg", "0336 339 339"),
        new Seed("Bún Riêu Tóp Mỡ Hương Sen", "Món Việt", "79 Ngõ 66 Phố Dịch Vọng Hậu, Cầu Giấy", 30_000, 50_000, 3.5, 21.0327, 105.7865, "07:30 - 21:00", "Bún riêu cua đồng, đậu rán, chả lá lốt.", "/images/bun-rieu-top-mo-huong-sen.jpg", "0359 412 389"),
        new Seed("Gogi House Trần Thái Tông", "Món Hàn", "103D5A, Trần Thái Tông, Cầu Giấy", 300_000, 700_000, 4.0, 21.0315, 105.7888, "10:00 - 22:00", "Thịt nướng kiểu Hàn, lẩu kimchi, rượu soju.", "/images/gogi-house.jpg", "024 7300 7392"),
        new Seed("Nhà Hàng Chay Đàm Hoa", "Món chay", "32 P.Dịch Vọng, Làng Vòng, Cầu Giấy", 55_000, 200_000, 4.5, 21.0335, 105.7867, "11:00 - 14:00 && 18:00 - 21:00", "Buffet chay nhỏ, nấm, đậu hũ, rau củ theo mùa.", "/images/nha-hang-chay-dam-hoa.jpg", "0354 577 648"),
        new Seed("YAKIMONO", "Lẩu", "170 P.Trần Bình, Từ Liêm", 200_000, 600_000, 4.5, 21.0315, 105.7768, "10:30 - 14:00 && 17:30 - 22:00", "Buffet lẩu nướng, hơn 60 món, phù hợp nhóm đông.", "/images/yakimono.jpg", "0914 108 986"),
        new Seed("The T Pastry", "Cafe & Bánh", "P2, CT16, KĐT Việt Hưng, Việt Hưng", 35_000, 100_000, 3.9, 21.0638, 105.9102, "07:30 - 22:00", "Bánh ngọt tự làm, cà phê rang xay, chỗ ngồi rộng.", "/images/the-t-pastry.jpg", "0353 299 666"),
        new Seed("Vịt Quay Da Giòn Phúc Hải", "Món Việt", "TT13 P.Hoàng Đôn Hòa, KĐT Văn Phú, Kiến Hưng", 50_000, 250_000, 4.9, 20.9554, 105.7688, "10:00 - 21:30", "Vịt quay da giòn, cơm rang, canh măng.", "/images/vit-quay-da-gion-phuc-hai.jpg", "0939 020 666"),
        new Seed("Nhà Hàng Cá Tầm Nga", "Lẩu", "Khu D, Lô D36 Geleximco, Lê Trọng Tấn, Dương Nội", 170_000, 290_000, 5.0, 20.9854, 105.748, "09:00 - 22:00", "Lẩu cá tầm măng chua, cá tươi nuôi tại bể.", "/images/nha-hang-ca-tam-nga.jpg", "0985 335 665"),
        new Seed("Gà Tươi Phạm Xá", "Món Việt", "HUD Vân Canh/25 Trịnh Văn Bô, Khu Chung Cư Number One, Sơn Đồng", 130_000, 260_000, 3.5, 21.0352, 105.7327, "Open 24 hours", "Gà đồi nướng, xôi nếp nương, không gian vườn rộng.", "/images/ga-tuoi-pham-xa.jpg", "0985 157 078"),
        new Seed("Nướng Ngói Số 1", "Món Việt", "36 P.Quảng Bá, Tây Hồ", 100_000, 220_000, 4.4, 21.0664, 105.8229, "17:00 - 23:00", "Thịt nướng ngói, rau rừng, dành cho nhóm bạn.", "/images/nuong-ngoi-so-1.jpg", "0000 000 001"),
        new Seed("Nhà Sàn Trên Hồ", "Hải sản", "7 Ngọc Hồi, Hoài Đức", 120_000, 300_000, 3.7, 20.9648, 105.8415, "07:00 - 23:30", "Cá sông nướng riềng mẻ, canh cá rau cần.", "/images/nha-san-tren-ho.jpg", "0978 273 023"),
        new Seed("Quán Nướng Rặng Tre Long", "Món Việt", "239 Đào Duy Tùng, Đài Bi, Đông Anh", 140_000, 280_000, 4.0, 21.1325, 105.8622, "10:00 - 22:00", "Nổi tiếng với thịt nướng tẩm ướp truyền thống ăn kèm bún cháy Mạch Tràng giòn rụm, giá bình dân và không gian sân vườn thoáng mát.", "/images/quan-nuong-rang-tre-long.jpg", "0981 526 368")
);
 
        List<Restaurant> entities = new java.util.ArrayList<>();
        int i = 1;
        for (Seed s : seeds) {
            Restaurant r = new Restaurant();
            r.setName(s.name());
            r.setCuisine(s.cuisine());
            r.setAddress(s.address());
            r.setPriceMin(s.min());
            r.setPriceMax(s.max());
            r.setRating(BigDecimal.valueOf(s.rating()));
            r.setLatitude(s.lat());
            r.setLongitude(s.lng());
            r.setOpeningHours(s.hours());
            r.setDescription(s.desc());
            r.setImageUrl(s.imageUrl());
            r.setPhone(s.phone());
            entities.add(r);
        }
 
        repository.saveAll(entities);
        log.info("Đã nạp {} nhà hàng mẫu tại Hà Nội", entities.size());
    }
}
 