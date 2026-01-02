import SwiftUI
import ComposeApp

class RootHolder: ObservableObject {
    let lifecycle: LifecycleRegistry
    let root: DefaultRootComponent
    
    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        
        MainViewControllerKt.doInitKoin()
        root = MainViewControllerKt.createRootComponent(lifecycle: lifecycle)
        
        LifecycleRegistryExtKt.create(lifecycle)
    }
    
    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}

@main
struct iOSApp: App {
    @StateObject var rootHolder = RootHolder()
    @Environment(\.scenePhase) var scenePhase
    
    var body: some Scene {
        WindowGroup {
            ContentView(component: rootHolder.root)
                .onChange(of: scenePhase) { newPhase in
                    switch newPhase {
                    case .background:
                        LifecycleRegistryExtKt.stop(rootHolder.lifecycle)
                    case .inactive:
                        LifecycleRegistryExtKt.pause(rootHolder.lifecycle)
                    case .active:
                        LifecycleRegistryExtKt.resume(rootHolder.lifecycle)
                    @unknown default:
                        break
                    }
                }
        }
    }
}
