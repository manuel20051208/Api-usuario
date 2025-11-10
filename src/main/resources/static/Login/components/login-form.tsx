"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Eye, EyeOff } from "lucide-react"

export function LoginForm() {
  const router = useRouter()
  const [isRegistering, setIsRegistering] = useState(false)
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

 const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault()
  setIsLoading(true)

  // Datos a enviar
  const payload = { email, password }

  try {
    // Decide endpoint según isRegistering
    const url = isRegistering
      ? "http://localhost:8080/api/usuario/registrar"
      : "http://localhost:8080/api/usuario/login"

    const response = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })

    // parsea json solo si es ok
    const data = response.ok ? await response.json() : null

    if (response.ok) {
      // alert con mensaje del backend
      alert(data?.mensaje || "Operación exitosa")

      // Si es login, redirige al dashboard
      if (!isRegistering) {
          localStorage.setItem("usuario", JSON.stringify({
              email: data.usuario.email}
          ));
          window.location.href = "http://localhost:3001"
      }

    } else {
      // Manejo de error según backend
      const errorMsg = data?.mensaje || await response.text()
      alert(errorMsg)
    }

  } catch (error) {
    console.error("Error durante la autenticación:", error)
    alert("Ocurrió un error al conectar con el servidor.")
  } finally {
    setIsLoading(false)
  }
}

  return (
    <div className="flex min-h-screen w-full items-center justify-center">
      <div className="w-full max-w-md px-4">
        {/* Logo/Header Section */}
        <div className="mb-8 text-center">
          <div className="mb-4 inline-flex h-20 w-20 items-center justify-center rounded-2xl bg-gradient-to-br from-[#8b5cf6] to-[#6366f1]">
            <svg className="h-10 w-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
              />
            </svg>
          </div>
          <h1 className="text-4xl font-bold text-white">{isRegistering ? "Crear Cuenta" : "Bienvenido"}</h1>
          <p className="mt-2 text-slate-400">
            {isRegistering ? "Regístrate para acceder a la plataforma" : "Inicia sesión en tu cuenta universitaria"}
          </p>  
        </div>

        {/* Login/Register Card */}
        <div className="rounded-2xl bg-[#1e293b] p-8 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="email" className="text-slate-200">
                Correo electrónico
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="estudiante@universidad.edu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={isLoading}
                className="h-12 border-slate-600 bg-[#0f172a] text-white placeholder:text-slate-500 focus:border-[#8b5cf6] focus:ring-[#8b5cf6]"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password" className="text-slate-200">
                Contraseña
              </Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  disabled={isLoading}
                  className="h-12 border-slate-600 bg-[#0f172a] pr-12 text-white placeholder:text-slate-500 focus:border-[#8b5cf6] focus:ring-[#8b5cf6]"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 transition-colors hover:text-slate-200"
                >
                  {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                </button>
              </div>
            </div>

            {isRegistering && (
              <div className="space-y-2">
                <Label htmlFor="confirmPassword" className="text-slate-200">
                  Confirmar contraseña
                </Label>
                <div className="relative">
                  <Input
                    id="confirmPassword"
                    type={showConfirmPassword ? "text" : "password"}
                    placeholder="••••••••"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    disabled={isLoading}
                    className="h-12 border-slate-600 bg-[#0f172a] pr-12 text-white placeholder:text-slate-500 focus:border-[#8b5cf6] focus:ring-[#8b5cf6]"
                  />
                  <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 transition-colors hover:text-slate-200"
                  >
                    {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                  </button>
                </div>
              </div>
            )}

            <Button
              type="submit"
              className="h-12 w-full bg-gradient-to-r from-[#8b5cf6] to-[#6366f1] text-lg font-semibold text-white hover:from-[#7c3aed] hover:to-[#4f46e5]"
              disabled={isLoading}
            >
              {isLoading
                ? isRegistering
                  ? "Registrando..."
                  : "Iniciando sesión..."
                : isRegistering
                  ? "Registrarse"
                  : "Iniciar sesión"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-slate-400">
              {isRegistering ? "¿Ya tienes una cuenta?" : "¿No tienes una cuenta?"}{" "}
              <button
                type="button"
                onClick={() => setIsRegistering(!isRegistering)}
                className="font-medium text-[#8b5cf6] hover:text-[#a78bfa]"
              >
                {isRegistering ? "Inicia sesión aquí" : "Regístrate aquí"}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}