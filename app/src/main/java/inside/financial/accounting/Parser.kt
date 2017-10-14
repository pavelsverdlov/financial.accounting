import android.icu.math.BigDecimal
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import inside.financial.accounting.OperationState
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Maxim on 10/14/2017.
 */

class Parser {

    class Input {
        lateinit var  pattern: String
       lateinit var inputText: String
    }

    inner class Output {
        lateinit var Amount: BigDecimal
        lateinit var Balance: BigDecimal
        lateinit var balanceUnit: String
        lateinit var amountUnit: String
        lateinit var Description: String
        lateinit var Card: String
        internal lateinit var Operation: OperationState
        lateinit var operationDate: Date


    }

    private class ReplaceReg (var inputString: String, var keyPattern: String) //replace keys to regex patterns

    private lateinit var out: Output
    fun parse(input: Input): Output {
        out = Output()
        genParseMthod(input)
        return out
    } // the interface method


    private fun genParseMthod(input: Input) {


        val p = Pattern.compile(exchange(input)) //generate parse pattern

        val m = p.matcher(input.inputText) // input parsing string

        while (m.find()) {// here happens filling output final class

            out.Card = m.group("Card")
            out.Amount = sumVerifying(m.group("Amount").trim())
            out.Balance = sumVerifying(m.group("Balance").trim())
            out.Operation = parseState(m.group("Operation").trim())
            out.balanceUnit = m.group("balanceUnit").trim()
            out.amountUnit = m.group("amountUnit").trim()
            out.Description = m.group("Description")
            out.operationDate = parseDate(m.group("Date"))!!



        }

    }

    private fun exchange(input: Input): String {
        val keys = Vector<ReplaceReg>()
        keys.add(ReplaceReg("\\{Card}", "(?<Card>$balance)"))
        keys.add(ReplaceReg("\\{Amount}", "(?<Amount>$balance)"))
        keys.add(ReplaceReg("\\{Date}", "(?<Date>$balance)"))
        keys.add(ReplaceReg("\\{Balance}", "(?<Balance>$balance)"))
        keys.add(ReplaceReg("\\{amountUnit}", "(?<amountUnit>$unit)"))
        keys.add(ReplaceReg("\\{balanceUnit}", "(?<balanceUnit>$unit)"))
        keys.add(ReplaceReg("\\{Operation}", "(?<Operation>$unit)"))
        keys.add(ReplaceReg("\\{Description}", "(?<Description>$unit)"))

        for (item in keys) {
            input.pattern = input.pattern.replace(item.inputString.toRegex(), item.keyPattern)
        }
        return input.pattern
    }

    private fun sumVerifying(sum: String): BigDecimal {
        var sum = sum
        sum = sum.replace("[\\s|,|-|*]".toRegex(), "")
        val d = java.lang.Double.parseDouble(sum)
        val result = BigDecimal.valueOf(d)
        return result
    } //convert string from input to digDecimal

    private fun parseDate(input: String): Date? {
        val datePatterns = ArrayList<SimpleDateFormat>()
        datePatterns.add(SimpleDateFormat("YYYY-dd-MM HH:mm:ss"))
        datePatterns.add(SimpleDateFormat("dd/MM HH:mm"))
        datePatterns.add(SimpleDateFormat("dd/MM/YYYY HH:mm"))
        var date: Date? = null

        for (parser in datePatterns) {

            try {
                date = parser.parse(input)
            } catch (e: ParseException) {
                continue
            }

        }
        return date
    }

    companion object {


        private val stateSuccessRegex = "\\[U|uspishn[a|o]"
        private val stateRejectRegx = "ne\\sdozvoleno|Vidmineno"
        private val balance = "\\\\d\\\\s?.*\\\\d"
        private val unit = "\\\\D.*\\\\D"


        private fun parseState(input: String): OperationState {
            try {
                var operMatch = Pattern.compile(stateSuccessRegex).matcher(input)
                if (operMatch.find()) {
                    return OperationState.SUCCESS
                }
                operMatch = Pattern.compile(stateRejectRegx).matcher(input)
                if (operMatch.find()) {
                    return OperationState.REJECTED
                }

            } catch (e: Exception) {
                return OperationState.UNDEFINDED
            }

            return OperationState.UNDEFINDED
        }
    }

}
