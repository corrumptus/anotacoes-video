"use client";

import FormResgistration from "@components/login/FormRegistration";
import { AuthenticationType } from "@hooks/useAuthenticate";
import useAuthenticationRedirection, { WhenRedirect } from "@hooks/useAuthenticationRedirection";

export default function SignUp() {
    useAuthenticationRedirection(WhenRedirect.AUTHENTICATED);

    return (
        <FormResgistration type={AuthenticationType.SIGNUP} />
    );
}