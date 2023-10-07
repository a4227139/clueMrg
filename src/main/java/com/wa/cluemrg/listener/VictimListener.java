package com.wa.cluemrg.listener;

import com.wa.cluemrg.dao.VictimMapper;
import com.wa.cluemrg.entity.Victim;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 导入处理监听
 */
@Log4j2
public class VictimListener extends CustomizeListener<Victim> {

    VictimMapper victimMapper;

    ThreadLocal<String> message;

    public VictimListener(Class<?> classType) {
        super(classType);
    }

    public VictimListener(Class<?> classType, VictimMapper victimMapper,ThreadLocal<String> message) {
        super(classType);
        this.victimMapper=victimMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {

        for (Victim victim:list){
            victim.setTime(LocalDateTime.now());
            int age = calculateAge(victim.getId());
            if (age!=-1){
                victim.setAge(age);
            }
            if (!StringUtils.isEmpty(victim.getDepartment())){
                if (victim.getDepartment().contains("城中")){
                    victim.setDepartment("城中分局");
                }else if (victim.getDepartment().contains("鱼峰")){
                    victim.setDepartment("鱼峰分局");
                }else if (victim.getDepartment().contains("柳南")){
                    victim.setDepartment("柳南分局");
                }else if (victim.getDepartment().contains("柳北")){
                    victim.setDepartment("柳北分局");
                }else if (victim.getDepartment().contains("柳江")){
                    victim.setDepartment("柳江分局");
                }else if (victim.getDepartment().contains("柳东")){
                    victim.setDepartment("柳东分局");
                }else if (victim.getDepartment().contains("柳城")){
                    victim.setDepartment("柳东县局");
                }else if (victim.getDepartment().contains("鹿寨")){
                    victim.setDepartment("鹿寨县局");
                }else if (victim.getDepartment().contains("融安")){
                    victim.setDepartment("融安县局");
                }else if (victim.getDepartment().contains("融水")){
                    victim.setDepartment("融水县局");
                }else if (victim.getDepartment().contains("三江")){
                    victim.setDepartment("三江县局");
                }
            }
        }
        int success = victimMapper.batchInsert(list);
        String result = "导入成功数："+success+" 导入失败数："+(list.size()-success);
        message.set(result);
        return result;
        /*list.forEach(item->{
            System.out.println(JSON.toJSONString(item));
        });*/
    }

    public int calculateAge(String idCardNumber) {
        if (!isValidIDCard(idCardNumber)){
            return -1;
        }
        // Extract the birthdate from the ID card number (assuming the format: YYYYMMDD)
        String birthdateString = idCardNumber.substring(6, 14);

        // Parse the birthdate into a Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date birthdate;
        try {
            birthdate = dateFormat.parse(birthdateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid ID card number format");
        }

        // Calculate age
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthdate);
        Calendar now = Calendar.getInstance();

        int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    public boolean isValidIDCard(String idCardNumber) {
        // Check length
        if (idCardNumber == null || idCardNumber.length() != 18) {
            return false;
        }

        // Check if all characters except the last one are digits
        for (int i = 0; i < 17; i++) {
            char c = idCardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        // Check the last character (should be a digit or 'X')
        char lastChar = idCardNumber.charAt(17);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // Check the format of the first 17 characters (YYYYMMDD)
        String birthdateString = idCardNumber.substring(6, 14);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false); // Strict date parsing
        try {
            Date birthdate = dateFormat.parse(birthdateString);
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false); // Strict date validation
            cal.setTime(birthdate);

            // Validate the birthdate
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // Month is 0-based
            int day = cal.get(Calendar.DAY_OF_MONTH);

            if (!(year >= 1900 && year <= cal.get(Calendar.YEAR)) ||
                    !(month >= 1 && month <= 12) ||
                    !(day >= 1 && day <= cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

        // Check the checksum
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int[] checkSumValues = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCardNumber.charAt(i) - '0') * weight[i];
        }
        int remainder = sum % 11;
        char expectedCheckSum = (char) checkSumValues[remainder];
        return (lastChar == expectedCheckSum);
    }

}
