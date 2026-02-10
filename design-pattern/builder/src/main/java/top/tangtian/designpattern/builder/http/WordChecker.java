package top.tangtian.designpattern.builder.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * @program: springboot-tech-stack
 * @description: 临时
 * @author: tangtian
 * @create: 2026-02-10 10:22
 **/
public class WordChecker {
    // 匹配行首的单词
    private static final Pattern WORD_PATTERN = Pattern.compile("^[a-zA-Z]+");

    public static void main(String[] args) throws Exception {
        var client = HttpClient.newHttpClient();
        var filePath = Path.of("E:download\\google\\words.txt");

        try (var lines = Files.lines(filePath)) {
            lines.map(line -> WORD_PATTERN.matcher(line))
                    .filter(matcher -> matcher.find())
                    .map(matcher -> matcher.group())
                    .forEach(word -> checkWord(client, word));
        }
    }

    private static void checkWord(HttpClient client, String word) {
        var jsonBody = """
                {"username":"%s"}
                """.formatted(word);

        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ai.com/user/username/check"))
                .header("Content-Type", "application/json")
                .header("cookie", "_cfuvid=pQhLBJL.KY.77ME3QEcmXb.vYJYFK8zE.wf2BMWMIK4-1770689065762-0.0.1.1-604800000; __stripe_mid=932001dc-a0f9-403a-ac99-ada5de8f2193472c44; __stripe_sid=2c9ba4b7-49ba-414d-8d71-72be444d7e919cd6ad; OptanonAlertBoxClosed=2026-02-10T02:04:39.735Z; _gcl_au=1.1.657498964.1770689080; _ga=GA1.1.821424556.1770689076; _fbp=fb.1.1770689080292.818501904814704750; _tt_enable_cookie=1; _ttp=01KH2MPBF8AB5JEQEX5JKYX5M0_.tt.1; token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIwMmRmMGU5OS0yNjM0LTQwMjQtOTJhMy1hMzMxZjUxYTRlY2YiLCJ1c2VyTmFtZSI6IueUnCDllJAiLCJpYXQiOjE3NzA2ODkwODksImV4cCI6MTc4NjI0MTA4OX0.ZY33pTaLcJUr_WpJ0drtRmXkbyqbICzGpdOMq0xjZhs; OptanonConsent=isGpcEnabled=0&datestamp=Tue+Feb+10+2026+10%3A04%3A51+GMT%2B0800+(China+Standard+Time)&version=202601.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=10471fa0-937a-4b3a-b42c-d4cfe1a3d012&interactionCount=2&isAnonUser=1&landingPath=NotLandingPage&groups=C0001%3A1%2CC0004%3A1%2CC0003%3A1%2CC0002%3A1&AwaitingReconsent=false&intType=1&geolocation=%3B; mp_2193df0576b59c0a09e5e09cd08868c1_mixpanel=%7B%22distinct_id%22%3A%22%24device%3Ae15695b8-3f2d-48b2-9afd-15eb23379971%22%2C%22%24device_id%22%3A%22e15695b8-3f2d-48b2-9afd-15eb23379971%22%2C%22%24initial_referrer%22%3A%22%24direct%22%2C%22%24initial_referring_domain%22%3A%22%24direct%22%2C%22__mps%22%3A%7B%22%24os%22%3A%22Windows%22%2C%22%24browser%22%3A%22Chrome%22%2C%22%24browser_version%22%3A144%2C%22has_card_on_file%22%3Afalse%7D%2C%22__mpso%22%3A%7B%22%24initial_referrer%22%3A%22%24direct%22%2C%22%24initial_referring_domain%22%3A%22%24direct%22%7D%2C%22__mpus%22%3A%7B%7D%2C%22__mpa%22%3A%7B%7D%2C%22__mpu%22%3A%7B%7D%2C%22__mpr%22%3A%5B%5D%2C%22__mpap%22%3A%5B%5D%2C%22%24search_engine%22%3A%22google%22%7D; ttcsid_D63O6HJC77UD936TQMEG=1770689080811::h8TqmR5fCVfAIed--QNW.1.1770689093294.1; ttcsid=1770689080811::yo9nhm8vabJRBc5PyO_g.1.1770689093295.0; _ga_SSPF7ZKNQW=GS2.1.s1770689076$o1$g1$t1770689094$j45$l0$h0$dOqBWUVfibHZaiVmudEpY0t8ZYCDM_lrTQw; __cf_bm=2XQEJZ2KfKZuhxs1u_0dFasYvxqsawONlPd8S6RP_y8-1770690051-1.0.1.1-pdR.FLtl5PMyjYOjLoKCogVLtpV_OwiqBaGjab2zcM7TeNtxZft0O4spAXE0ExOBD76PJHSJmTVt1NV3TxGJgMhG9Yx__ie7qSQvTlEXl0Y")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    if (res.body().contains("\"available\":true")) {
                        System.out.println("[可用] " + word);
                    } else {
                        System.out.println("[占用] " + word);
                    }
                })
                .join(); // 仅为演示方便同步等待，生产环境可优化
    }
}
