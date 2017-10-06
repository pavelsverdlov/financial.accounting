package inside.financial.accounting.presentation.main

/**
 * Created by Pasha on 10/6/2017.
 */
class MainPresenter(val view : MainContract.View) : MainContract.Presenter{
    init {
        view.presenter = this
    }
    override fun start() {
        //view.
    }

}