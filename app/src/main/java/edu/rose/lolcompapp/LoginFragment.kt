package edu.rose.lolcompapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.login_page.view.*

class LoginFragment: Fragment() {

    var listener: OnLoginButtonPressedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_page, container, false)
        view.login_button.setOnClickListener {
            listener?.onLoginButtonPressed()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginButtonPressedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement the listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnLoginButtonPressedListener {
        fun onLoginButtonPressed()
    }
}