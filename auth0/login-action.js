/**
 * Auth0 Login Action: whitelist + email MFA when the email is verified.
 *
 * Leave One-time Password ON. Enable Email. Turn on
 * Security → Multi-factor Auth → Additional Settings → Customize MFA Factors using Actions.
 *
 * Do not call challengeWith('email') until email_verified is true, or Auth0 shows
 * "Two-factor authentication is required... contact your system administrator".
 */
exports.onExecutePostLogin = async (event, api) => {
  const allowed = [
    "careers@fidenz.com",
    "buddhinikaluwila1999@gmail.com",
  ];
  const email = (event.user.email || "").toLowerCase();
  if (!allowed.includes(email)) {
    api.access.deny("This email is not on the allow list.");
    return;
  }

  if (event.user.email_verified) {
    api.authentication.challengeWith({ type: "email" });
  }
};
