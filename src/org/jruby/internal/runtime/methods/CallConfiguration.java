/*
 * CallConfiguration.java
 * 
 * Created on Jul 13, 2007, 6:51:14 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jruby.internal.runtime.methods;

import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author headius
 */
public enum CallConfiguration {
    FrameFullScopeFull(Framing.Full, Scoping.Full) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodFrameAndScope(implementer, name, self, block, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodFrameAndScope();
        }
    },
    FrameFullScopeDummy(Framing.Full, Scoping.Dummy) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodFrameAndDummyScope(implementer, name, self, block, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodFrameAndScope();
        }
    },
    FrameFullScopeNone (Framing.Full, Scoping.None) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodFrameOnly(implementer, name, self, block);
        }
        public void post(ThreadContext context) {
            context.postMethodFrameOnly();
        }
    },
    FrameBacktraceScopeFull (Framing.Backtrace, Scoping.Full) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodBacktraceAndScope(name, implementer, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodBacktraceAndScope();
        }
    },
    FrameBacktraceScopeDummy (Framing.Backtrace, Scoping.Dummy) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodBacktraceDummyScope(implementer, name, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodBacktraceDummyScope();
        }
    },
    FrameBacktraceScopeNone (Framing.Backtrace, Scoping.None) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodBacktraceOnly(name);
        }
        public void post(ThreadContext context) {
            context.postMethodBacktraceOnly();
        }
    },
    FrameNoneScopeFull(Framing.None, Scoping.Full) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodScopeOnly(implementer, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodScopeOnly();
        }
    },
    FrameNoneScopeDummy(Framing.None, Scoping.Dummy) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
            context.preMethodNoFrameAndDummyScope(implementer, scope);
        }
        public void post(ThreadContext context) {
            context.postMethodScopeOnly();
        }
    },
    FrameNoneScopeNone(Framing.None, Scoping.None) {
        public void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope) {
        }
        public void post(ThreadContext context) {
        }
    };

    @Deprecated
    public static final CallConfiguration FRAME_AND_SCOPE = FrameFullScopeFull;
    @Deprecated
    public static final CallConfiguration FRAME_AND_DUMMY_SCOPE = FrameFullScopeDummy;
    @Deprecated
    public static final CallConfiguration FRAME_ONLY = FrameFullScopeNone;
    @Deprecated
    public static final CallConfiguration BACKTRACE_AND_SCOPE = FrameBacktraceScopeFull;
    @Deprecated
    public static final CallConfiguration BACKTRACE_DUMMY_SCOPE = FrameBacktraceScopeNone;
    @Deprecated
    public static final CallConfiguration BACKTRACE_ONLY = FrameBacktraceScopeNone;
    @Deprecated
    public static final CallConfiguration SCOPE_ONLY = FrameNoneScopeFull;
    @Deprecated
    public static final CallConfiguration NO_FRAME_DUMMY_SCOPE = FrameNoneScopeDummy;
    @Deprecated
    public static final CallConfiguration NO_FRAME_NO_SCOPE = FrameNoneScopeNone;
    
    public static CallConfiguration getCallConfigByAnno(JRubyMethod anno) {
        return getCallConfig(anno.frame(), anno.scope(), anno.backtrace());
    }
    
    public static CallConfiguration getCallConfig(boolean frame, boolean scope, boolean backtrace) {
        if (frame) {
            if (scope) {
                return FrameFullScopeFull;
            } else {
                return FrameFullScopeNone;
            }
        } else if (scope) {
            if (backtrace) {
                return FrameBacktraceScopeFull;
            } else {
                return FrameNoneScopeFull;
            }
        } else if (backtrace) {
            return FrameBacktraceScopeNone;
        } else {
            return FrameNoneScopeNone;
        }
    }

    private final Framing framing;
    private final Scoping scoping;

    CallConfiguration(Framing framing, Scoping scoping) {
        this.framing = framing;
        this.scoping = scoping;
    }

    public final Framing framing() {return framing;}
    public final Scoping scoping() {return scoping;}
    
    public abstract void pre(ThreadContext context, IRubyObject self, RubyModule implementer, String name, Block block, StaticScope scope);
    public abstract void post(ThreadContext context);
    boolean isNoop() { return framing == Framing.None && scoping == Scoping.None; }
}
