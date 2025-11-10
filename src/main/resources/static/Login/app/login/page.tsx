import { LoginForm } from "@/components/login-form"

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#0f172a] p-4">
      <div className="absolute inset-0 bg-gradient-to-br from-[#1e293b] via-[#0f172a] to-[#0f172a]" />
      <div className="relative z-10">
        <LoginForm />
      </div>
    </div>
  )
}
