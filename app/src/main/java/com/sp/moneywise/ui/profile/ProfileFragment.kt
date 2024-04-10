package com.sp.moneywise.ui.profile

import ProfileViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sp.moneywise.ui.authentication.login.LoginActivity
import com.sp.moneywise.ui.dashboard.OnLoadingUIUpdate
import com.sp.moneywise.R
import com.sp.moneywise.databinding.FragmentProfileBinding
import com.sp.moneywise.longToastShow
import com.sp.moneywise.validateEmail
import com.sp.moneywise.validatePassword

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val profileViewModel: ProfileViewModel by viewModels<ProfileViewModel>()
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private var loadingUIUpdate: OnLoadingUIUpdate? = null

    private var actionCallbacks = object : ProfileFragmentBaseObservable.ProfileCallbacks {
        override fun onLogOutClicked() {
            // Logout User
            profileViewModel.logout()
            activity?.longToastShow("Logout Successful")
            val mainIntent = Intent(activity, LoginActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainIntent)
            activity?.finish()
        }

        override fun onDeleteAccountClicked() {
            loadingUIUpdate?.showLoading()
            profileViewModel.deleteAccount { result, exception ->
                if (result) {
                    activity?.run {
                        loadingUIUpdate?.dismiss()
                        longToastShow("Delete Account Successful")
                        val mainIntent = Intent(this, LoginActivity::class.java)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainIntent)
                        finish()
                    }
                } else {
                    loadingUIUpdate?.dismiss()
                    exception?.printStackTrace()
                    exception?.message?.let { it1 -> activity?.longToastShow(it1) }
                }

            }
        }

        override fun onSettingsClicked() {
            activity?.longToastShow("settings clicked")
        }

        override fun onExportDataClicked() {
            activity?.longToastShow("export CLicked")
        }

        override fun updateEmail() {
            if (validateEmail(fragmentProfileBinding.edOriginalEmail, fragmentProfileBinding.inputOriginalEmail)
                && validatePassword(fragmentProfileBinding.edNewEmail, fragmentProfileBinding.inputNewEmail)
                && validatePassword(fragmentProfileBinding.edOriginalPassword, fragmentProfileBinding.inputOriginalPassword)
            ) {
                loadingUIUpdate?.showLoading()
                val originalEmail = fragmentProfileBinding.edOriginalEmail.text.toString()
                val password = fragmentProfileBinding.edOriginalPassword.text.toString()
                val newEmail = fragmentProfileBinding.edNewEmail.text.toString()
                profileViewModel.updateEmail(
                    originalEmail = originalEmail,
                    password = password,
                    newEmail = newEmail
                ) { result, exception ->
                    if (result) {
                        activity?.run {
                            loadingUIUpdate?.dismiss()
                            longToastShow("Email id Updated Successfully")
                        }
                    } else {
                        loadingUIUpdate?.dismiss()
                        exception?.printStackTrace()
                        exception?.message?.let { it1 -> activity?.longToastShow(it1) }
                    }
                }
            }
        }

        override fun updatePassword() {
            if (validateEmail(fragmentProfileBinding.edOriginalEmail, fragmentProfileBinding.inputOriginalEmail)
                && validatePassword(fragmentProfileBinding.edOriginalPassword, fragmentProfileBinding.inputOriginalPassword)
                && validatePassword(fragmentProfileBinding.edNewPassword, fragmentProfileBinding.inputNewPassword)
            ) {
                loadingUIUpdate?.showLoading()
                val originalEmail = fragmentProfileBinding.edOriginalEmail.text.toString()
                val oldPassword = fragmentProfileBinding.edOriginalPassword.text.toString()
                val newPassword = fragmentProfileBinding.edNewPassword.text.toString()
                profileViewModel.updatePassword(
                    email = originalEmail,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                ) { result, exception ->
                    if (result) {
                        activity?.run {
                            loadingUIUpdate?.dismiss()
                            longToastShow("Password Updated Successfully")
                        }
                    } else {
                        loadingUIUpdate?.dismiss()
                        exception?.printStackTrace()
                        exception?.message?.let { it1 -> activity?.longToastShow(it1) }
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        fragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        fragmentProfileBinding.callback = actionCallbacks
        loadingUIUpdate = activity  as? OnLoadingUIUpdate
        return fragmentProfileBinding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}