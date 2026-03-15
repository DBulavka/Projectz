declare const angular: any;

type Dict = Record<string, unknown>;

const app = angular.module('workflowApp', []);

app.controller('MainCtrl', function MainCtrl(this: any, $http: any, $timeout: any) {
  const vm = this;
  vm.apiBase = 'http://localhost:8080';
  vm.token = localStorage.getItem('token') || '';
  vm.message = '';

  vm.loginForm = { email: 'demo', password: 'demo' };
  vm.registerForm = { email: '', password: '' };

  vm.processes = [];
  vm.selectedProcessId = '';
  vm.processForm = { key: '', name: '', description: '', category: 'default' };

  vm.versions = [];
  vm.selectedVersionId = '';
  vm.versionForm = { bpmnXml: defaultBpmn() };

  vm.instances = [];
  vm.startForm = { processId: '', businessKey: '', variables: '{"assignee":"demo"}' };

  vm.tasks = [];
  vm.taskVariables = '{"approved":true}';

  const headers = () => ({ headers: { Authorization: `Bearer ${vm.token}` } });
  const notify = (msg: string) => {
    vm.message = msg;
    $timeout(() => {
      if (vm.message === msg) vm.message = '';
    }, 3000);
  };

  function defaultBpmn() {
    return `<?xml version="1.0" encoding="UTF-8"?>\n<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" targetNamespace="Example">\n  <process id="new-process" isExecutable="true">\n    <startEvent id="start" />\n    <endEvent id="end" />\n    <sequenceFlow id="flow1" sourceRef="start" targetRef="end" />\n  </process>\n</definitions>`;
  }

  function safeJson(value: string): Dict | null {
    try {
      return value ? JSON.parse(value) : {};
    } catch {
      notify('Невалидный JSON');
      return null;
    }
  }

  vm.login = () => {
    $http.post(`${vm.apiBase}/api/auth/login`, vm.loginForm).then((res: any) => {
      vm.token = res.data.token;
      localStorage.setItem('token', vm.token);
      notify('Вход выполнен');
      vm.loadAll();
    }, () => notify('Ошибка входа'));
  };

  vm.register = () => {
    $http.post(`${vm.apiBase}/api/auth/register`, vm.registerForm).then(() => notify('Пользователь создан'), () => notify('Ошибка регистрации'));
  };

  vm.logout = () => {
    vm.token = '';
    localStorage.removeItem('token');
  };

  vm.loadAll = () => {
    vm.loadProcesses();
    vm.loadInstances();
    vm.loadTasks();
  };

  vm.loadProcesses = () => {
    $http.get(`${vm.apiBase}/api/processes`, headers()).then((res: any) => vm.processes = res.data);
  };

  vm.selectProcess = (id: string) => {
    vm.selectedProcessId = id;
    $http.get(`${vm.apiBase}/api/processes/${id}`, headers()).then((res: any) => vm.processForm = res.data);
    vm.loadVersions();
  };

  vm.createProcess = () => {
    $http.post(`${vm.apiBase}/api/processes`, vm.processForm, headers()).then(() => {
      vm.processForm = { key: '', name: '', description: '', category: 'default' };
      vm.loadProcesses();
      notify('Процесс создан');
    });
  };

  vm.updateProcess = () => {
    $http.put(`${vm.apiBase}/api/processes/${vm.selectedProcessId}`, vm.processForm, headers()).then(() => {
      vm.loadProcesses();
      notify('Процесс обновлён');
    });
  };

  vm.loadVersions = () => {
    if (!vm.selectedProcessId) return;
    $http.get(`${vm.apiBase}/api/processes/${vm.selectedProcessId}/versions`, headers()).then((res: any) => vm.versions = res.data);
  };

  vm.selectVersion = (id: string) => {
    vm.selectedVersionId = id;
    $http.get(`${vm.apiBase}/api/processes/${vm.selectedProcessId}/versions/${id}`, headers()).then((res: any) => vm.versionForm.bpmnXml = res.data.bpmnXml);
  };

  vm.createVersion = () => {
    $http.post(`${vm.apiBase}/api/processes/${vm.selectedProcessId}/versions`, { bpmnXml: vm.versionForm.bpmnXml }, headers()).then(() => {
      vm.loadVersions();
      notify('Версия создана');
    });
  };

  vm.saveVersion = () => {
    $http.put(`${vm.apiBase}/api/processes/${vm.selectedProcessId}/versions/${vm.selectedVersionId}/bpmn`, { bpmnXml: vm.versionForm.bpmnXml }, headers()).then(() => {
      vm.loadVersions();
      notify('BPMN сохранён');
    });
  };

  vm.publishVersion = () => {
    $http.post(`${vm.apiBase}/api/processes/${vm.selectedProcessId}/versions/${vm.selectedVersionId}/publish`, {}, headers()).then(() => {
      vm.loadVersions();
      notify('Версия опубликована');
    });
  };

  vm.loadInstances = () => {
    $http.get(`${vm.apiBase}/api/instances`, headers()).then((res: any) => vm.instances = res.data);
  };

  vm.startInstance = () => {
    const parsed = safeJson(vm.startForm.variables);
    if (parsed === null || !vm.startForm.processId) return;
    $http.post(`${vm.apiBase}/api/processes/${vm.startForm.processId}/start`, {
      businessKey: vm.startForm.businessKey || null,
      variables: parsed
    }, headers()).then(() => {
      vm.loadInstances();
      notify('Инстанс запущен');
    });
  };

  vm.cancelInstance = (id: string) => {
    $http.post(`${vm.apiBase}/api/instances/${id}/cancel`, {}, headers()).then(() => {
      vm.loadInstances();
      notify('Инстанс отменён');
    });
  };

  vm.loadTasks = () => {
    $http.get(`${vm.apiBase}/api/tasks/my`, headers()).then((res: any) => vm.tasks = res.data);
  };

  vm.completeTask = (id: string) => {
    const parsed = safeJson(vm.taskVariables);
    if (parsed === null) return;
    $http.post(`${vm.apiBase}/api/tasks/${id}/complete`, { variables: parsed }, headers()).then(() => {
      vm.loadTasks();
      notify('Задача завершена');
    });
  };

  if (vm.token) vm.loadAll();
});
