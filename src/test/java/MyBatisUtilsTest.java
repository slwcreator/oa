import com.slwer.oa.utils.MyBatisUtils;
import org.junit.Test;

public class MyBatisUtilsTest {
    @Test
    public void testCase1() {
        String result = (String) MyBatisUtils.executeQuery(sqlSession -> sqlSession.selectOne("test.sample"));
        System.out.println(result);
    }
}