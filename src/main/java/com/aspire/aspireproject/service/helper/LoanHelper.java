package com.aspire.aspireproject.service.helper;

import com.aspire.aspireproject.model.loan.Loan;
import com.aspire.aspireproject.model.loan.LoanStatus;
import com.aspire.aspireproject.model.loan.ScheduledLoanRepayment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class LoanHelper {
    public List<ScheduledLoanRepayment> createRePayments(double amount, int term, Date date){
        List<ScheduledLoanRepayment> list = new ArrayList<>();
        double repaymentAmount = amount / term;
        while(term>0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, term);
            Date termDate = calendar.getTime();
            var scheduledLoan = ScheduledLoanRepayment.builder().remainingAmount(repaymentAmount).termNo(term).status(LoanStatus.PENDING).date(termDate).build();
            term--;
            list.add(scheduledLoan);
        }
        return list;
    }
}
