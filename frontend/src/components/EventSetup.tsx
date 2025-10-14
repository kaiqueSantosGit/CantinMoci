import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Calendar, TrendingUp, LogOut } from "lucide-react";
import { getEvents, createEvent } from "../services/api";

interface Event {
  id: string;
  nome: string;
  ano: number;
  regional: string;
  tipo: string;
}

interface EventSetupProps {
  accessToken: string;
  userId: string; // <-- ADICIONE ESTA LINHA
  onEventSelected: (eventId: string) => void;
  onLogout: () => void;
}

export default function EventSetup({
  accessToken,
  onEventSelected,
  onLogout,
}: EventSetupProps) {
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [formData, setFormData] = useState({
    regional: "",
    tipo: "",
    nome: "",
    ano: new Date().getFullYear().toString(),
  });

  // useEffect para carregar os eventos assim que o componente montar
  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      setLoading(true);
      const data = await getEvents(accessToken);
      setEvents(data.eventos || []);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // Prepara os dados para enviar, tratando o nome do evento customizado
      const eventPayload = {
        regional: formData.regional,
        tipo: formData.tipo,
        nome:
          formData.tipo === "customizado"
            ? formData.nome
            : formData.tipo.toUpperCase(),
        ano: parseInt(formData.ano),
      };

      const newEvent = await createEvent(eventPayload, accessToken);
      onEventSelected(newEvent.id); // Redireciona para o evento recém-criado
    } catch (err: any) {
      setError(err.message);
      setLoading(false);
    }
    // Não definimos setLoading(false) no 'finally' aqui, pois a tela vai mudar
  };

  if (loading && events.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p>Carregando eventos...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-4 md:p-8">
      <div className="max-w-5xl mx-auto">
        <header className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-800">CantinMoci</h1>
            <p className="text-sm text-gray-600">Gerenciamento de Eventos</p>
          </div>
          <Button
            variant="ghost"
            onClick={onLogout}
            className="gap-2 text-gray-600"
          >
            <LogOut size={16} /> Sair
          </Button>
        </header>

        {error && (
          <Alert variant="destructive" className="mb-6">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        <div className="grid md:grid-cols-2 gap-8">
          {/* Formulário de Novo Evento */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar size={20} /> Novo Evento
              </CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleCreateEvent} className="space-y-4">
                <div>
                  <label className="text-sm font-medium text-gray-700 mb-1 block">
                    Sua Regional
                  </label>
                  <Input
                    type="text"
                    placeholder="Ex: Regional São Paulo"
                    value={formData.regional}
                    onChange={(e) =>
                      setFormData({ ...formData, regional: e.target.value })
                    }
                    required
                  />
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-700 mb-1 block">
                    Tipo do Evento
                  </label>
                  <Select
                    value={formData.tipo}
                    onValueChange={(value) =>
                      setFormData({ ...formData, tipo: value })
                    }
                    required
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione o tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="EGM">EGM</SelectItem>
                      <SelectItem value="ERM">ERM</SelectItem>
                      <SelectItem value="customizado">
                        Outro Evento (Regional)
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                {formData.tipo === "customizado" && (
                  <div>
                    <label className="text-sm font-medium text-gray-700 mb-1 block">
                      Nome do Evento
                    </label>
                    <Input
                      type="text"
                      placeholder="Ex: Festival Beneficente"
                      value={formData.nome}
                      onChange={(e) =>
                        setFormData({ ...formData, nome: e.target.value })
                      }
                      required
                    />
                  </div>
                )}
                <div>
                  <label className="text-sm font-medium text-gray-700 mb-1 block">
                    Ano do Evento
                  </label>
                  <Input
                    type="number"
                    min="2024"
                    value={formData.ano}
                    onChange={(e) =>
                      setFormData({ ...formData, ano: e.target.value })
                    }
                    required
                  />
                </div>
                <Button type="submit" className="w-full" disabled={loading}>
                  {loading ? "Criando..." : "Criar e Acessar Evento"}
                </Button>
              </form>
            </CardContent>
          </Card>

          {/* Histórico de Eventos */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <TrendingUp size={20} /> Acessar Evento Anterior
              </CardTitle>
            </CardHeader>
            <CardContent>
              {events.length === 0 ? (
                <p className="text-gray-500 text-center py-8">
                  Nenhum evento cadastrado ainda.
                </p>
              ) : (
                <div className="space-y-3">
                  {events.map((event) => (
                    <div
                      key={event.id}
                      className="border rounded-lg p-3 flex justify-between items-center hover:bg-gray-50"
                    >
                      <div>
                        <h3 className="font-semibold text-gray-800">
                          {event.nome}
                        </h3>
                        <p className="text-sm text-gray-600">
                          {event.regional} - {event.ano}
                        </p>
                      </div>
                      <Button
                        size="sm"
                        onClick={() => onEventSelected(event.id)}
                      >
                        Acessar
                      </Button>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
