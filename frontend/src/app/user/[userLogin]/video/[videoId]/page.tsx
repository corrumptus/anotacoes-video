"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect } from "react";

export default function UserVideoPage() {
    const router = useRouter();
    const { videoId } = useParams<{ userLogin: string, videoId: string }>();

    useEffect(() => {
        router.push("/video/" + videoId);
    });

    return null;
}