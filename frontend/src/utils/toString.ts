import { AuthenticationType } from "@hooks/useAuthenticate";
import { VisibilityType } from "@hooks/useVideoStore";

export function visibilityToString(visibility: VisibilityType) {
    switch (visibility) {
        case VisibilityType.PRIVATE: return "privado";
        case VisibilityType.RESTRICTED: return "compartilhado";
        case VisibilityType.PUBLIC: return "todos com o link";
        default: throw new Error("Not implemented");
    }
}

export function authenticationTypeToString(type: AuthenticationType) {
    switch (type) {
        case AuthenticationType.LOGIN: return "Entrar";
        case AuthenticationType.SIGNUP: return "Cadastrar";
        default: throw new Error("Not implemented");
    }
}