/*	
====================================================================================
2011 | Jonathan Baker  | JazzGen  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ JazzGen {
	
	loadSynths {
	{
	SynthDef(\buff, {|bufnum, rate = 1, trigrate = 0.0001, startpos = 0, freq = 4000, amp = 0.8, sustain = 0.125, release = 0.01,ampMult=1|
		var trig, sound, filt,vol;
		
		vol=amp*ampMult;
		trig = Impulse.ar(trigrate);
		
		sound = PlayBuf.ar(1,bufnum,rate*BufRateScale.kr(bufnum),trig,startpos,doneAction:2);
		Out.ar(0,Pan2.ar(sound*vol))
	}).store;
	

	SynthDef(\piano, { |out=0, note=60, gate=1, sustain = 0.6, release = 0.3, amp=0.125, ampMult=1,vel=100|
		var sig, freq, vol;
			
			//"piano note: ".post; note.postln;
			vol = amp*ampMult;
			freq = note.midicps;
			//freq = if(note!=nil,{note.midicps},{"nil value recieved piano!!!!!!!".postln;0});
			sig = MdaPiano.ar(freq, gate, vel, decay:sustain, release: release, stereo: 0.3, sustain: 0);
			sig = FreeVerb.ar(sig,0.25,0.5,0.15);
			DetectSilence.ar(sig, 0.0001, doneAction:2);
			Out.ar(out,Pan2.ar( sig *vol));
		}).store;
		
		
	SynthDef(\bass, {|note = 60, amp = 0.6, pluckRel=0.25,bassRel=0.5,formFreq = 200, bwFreq=200, ampMult=1|
		var env1, env2, freq, signal,vol;

		//note.postln;
		freq = note.midicps;
		//freq = if(note!=nil,{note.midicps},{"nil value recieved bass!!!!!!!".postln;0});
		vol = amp*ampMult;
		
		env1 = EnvGen.ar(Env.perc(0,pluckRel,(amp*0.65),-10));
		env2 = EnvGen.ar(Env.perc(0.1,bassRel,(amp*0.65),-4));
		
		signal = Formant.ar(freq, XLine.kr(200,formFreq,1.5), XLine.kr(200,bwFreq,0.9), env1);
		signal = signal + LFPar.ar(freq,0,env2);
		signal = signal + (LFPar.ar((freq*2),0,env2)*0.5);
		DetectSilence.ar(signal, 0.01, doneAction:2);
		
		Out.ar(0,Pan2.ar(signal*vol))
	}).store;

	s.sync;
	}.fork;

	}

}