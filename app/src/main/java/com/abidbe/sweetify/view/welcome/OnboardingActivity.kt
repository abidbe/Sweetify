package com.abidbe.sweetify.view.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.abidbe.sweetify.R
import com.abidbe.sweetify.databinding.ActivityOnboardingBinding
import com.abidbe.sweetify.view.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var buttonNext: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        buttonNext = binding.buttonNext

        val pages = listOf(
            OnboardingPage(
                R.drawable.undraw_donut_love,
                getString(R.string.title_onboarding1),
                getString(R.string.desc_onboarding1)
            ),
            OnboardingPage(
                R.drawable.undraw_photo,
                getString(R.string.title_onboarding2),
                getString(R.string.desc_onboarding2)
            ),
            OnboardingPage(
                R.drawable.undraw_report,
                getString(R.string.title_onboarding3),
                getString(R.string.desc_onboarding3)
            )
        )

        val adapter = OnboardingAdapter(pages)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        }.attach()

        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(10, 0, 10, 0)
            tab.requestLayout()
        }

        buttonNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem + 1 < pages.size) {
                viewPager.currentItem = currentItem + 1
            } else {
                signIn()
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == pages.size - 1) {
                    buttonNext.text = getString(R.string.login_google)
                } else {
                    buttonNext.text = getString(R.string.next)
                }
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        showLoading()
        val credentialManager = CredentialManager.create(this)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@OnboardingActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
                hideLoading()
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        hideLoading()
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                    hideLoading()
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
                hideLoading()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                hideLoading()
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun showLoading() {
        binding.progressIndicator.visibility = View.VISIBLE
        binding.container.alpha = 0.5f
    }

    private fun hideLoading() {
        binding.progressIndicator.visibility = View.GONE
        binding.container.alpha = 1f
    }


    companion object {
        private const val TAG = "OnboardingActivity"
    }
}