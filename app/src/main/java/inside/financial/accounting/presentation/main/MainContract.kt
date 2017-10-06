package inside.financial.accounting.presentation.main

import inside.financial.accounting.BasePresenter
import inside.financial.accounting.BaseView

/**
 * Created by Pasha on 10/6/2017.
 */

interface MainContract{
    interface Presenter : BasePresenter {

    }
    interface View : BaseView<Presenter> {

    }
}