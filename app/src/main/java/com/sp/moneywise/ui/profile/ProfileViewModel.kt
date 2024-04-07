import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()


    fun logout() {
        firebaseAuth.signOut()

    }

    fun deleteAccount(callbacks: (Boolean, Exception?) -> Unit) {
        firebaseAuth.currentUser?.delete()
            ?.addOnSuccessListener {
                callbacks.invoke(true, null)
            }
            ?.addOnFailureListener {
                callbacks.invoke(false, it)

            }
    }

    fun updateEmail(
        originalEmail: String,
        password: String,
        newEmail: String,
        callbacks: (Boolean, Exception?) -> Unit,
    ) {
        val credentials = EmailAuthProvider.getCredential(
            originalEmail.trim(),
            password.trim()
        )
        firebaseAuth.currentUser?.reauthenticate(credentials)?.addOnSuccessListener {
            firebaseAuth.currentUser?.updateEmail(
                newEmail.trim()
            )?.addOnSuccessListener {
                callbacks.invoke(true, null)

            }?.addOnFailureListener {
                callbacks.invoke(false, it)

            }
        }?.addOnFailureListener {
            callbacks.invoke(false, it)

        }
    }

    fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String,
        callbacks: (Boolean, Exception?) -> Unit,
    ) {

        val credentials = EmailAuthProvider.getCredential(
            email.trim(),
            oldPassword.trim()
        )
        firebaseAuth.currentUser?.reauthenticate(credentials)?.addOnSuccessListener {
            firebaseAuth.currentUser?.updatePassword(
                newPassword.trim()
            )?.addOnSuccessListener {
                callbacks.invoke(true, null)
            }?.addOnFailureListener {
                callbacks.invoke(false, it)
            }
        }?.addOnFailureListener {
            callbacks.invoke(false, it)
        }
    }


}